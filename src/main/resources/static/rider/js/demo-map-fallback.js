/**
 * 카카오 지도 SDK 없이도 라이더 배달 화면이 돌게 하는 대체 구현.
 *
 * 데모 프로파일은 카카오 키가 없어 SDK 스크립트가 window.kakao 를 만들지 못한다.
 * 그러면 initMap 의 첫 줄에서 예외가 나 주문 목록 조회부터 경로 계산까지 전부 멈춘다.
 * 여기서는 화면이 실제로 쓰는 만큼만 같은 이름으로 채워 넣고, 지도는 외부 라이브러리 없이
 * 인라인 SVG 로 그린다. SDK 가 떠 있으면 아무것도 건드리지 않는다.
 *
 * 좌표 변환은 서버 데모 지오코더에 맡긴다. 브라우저에서 따로 계산하면 서비스 구역 경계를
 * 두 곳에서 관리하게 된다.
 */
(function (global, document) {
    "use strict";

    var GEOCODE_URL = "/rider/route/geocode/demo";
    var MIN_MAP_HEIGHT = 560;
    var VIEW_PADDING_RATIO = 0.08;
    var MIN_SPAN_DEGREES = 0.004;

    function unwrap(body) {
        if (body && typeof body === "object" && body.success !== undefined && body.data !== undefined) {
            return body.data;
        }
        return body;
    }

    var cache = {};

    /** 주소마다 한 번만 서버에 묻는다. 같은 가게 주소가 카드 수만큼 반복해서 들어온다. */
    var DemoGeocoder = {
        locate: function (address, callback) {
            var key = (address || "").trim();
            if (!key) {
                callback(null);
                return;
            }
            if (Object.prototype.hasOwnProperty.call(cache, key)) {
                callback(cache[key]);
                return;
            }
            fetch(GEOCODE_URL, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({addresses: [key]})
            }).then(function (response) {
                if (!response.ok) {
                    throw new Error("데모 지오코더 응답 코드 " + response.status);
                }
                return response.json();
            }).then(function (body) {
                var points = unwrap(body);
                var point = (points && points.length) ? points[0] : null;
                cache[key] = point;
                callback(point);
            }).catch(function (error) {
                console.warn("[DemoGeocoder] 좌표를 구하지 못했습니다.", error);
                cache[key] = null;
                callback(null);
            });
        }
    };

    global.DemoGeocoder = DemoGeocoder;

    if (global.kakao && global.kakao.maps && typeof global.kakao.maps.Map === "function") {
        return;
    }

    console.warn("[demo-map-fallback] 카카오 지도 SDK가 없어 자체 렌더링으로 대체합니다.");

    var SVG_NS = "http://www.w3.org/2000/svg";

    function LatLng(lat, lng) {
        this._lat = Number(lat);
        this._lng = Number(lng);
    }

    LatLng.prototype.getLat = function () {
        return this._lat;
    };

    LatLng.prototype.getLng = function () {
        return this._lng;
    };

    function Size(width, height) {
        this.width = width;
        this.height = height;
    }

    function PointXY(x, y) {
        this.x = x;
        this.y = y;
    }

    function MarkerImage(src, size) {
        this.src = src;
        this.size = size || new Size(32, 32);
    }

    function FallbackMap(container, options) {
        this._container = container;
        this._center = (options && options.center) || new LatLng(0, 0);
        this._polylines = [];
        this._markers = [];
        this._overlays = [];
        this._frame = null;

        if (getComputedStyle(container).position === "static") {
            container.style.position = "relative";
        }
        // SDK 가 만들던 높이가 없으면 컨테이너가 0으로 접혀 아무것도 보이지 않는다.
        if (container.clientHeight < 80) {
            container.style.height = MIN_MAP_HEIGHT + "px";
        }
        container.style.background = "#eef1f5";
        container.style.overflow = "hidden";

        this._svg = document.createElementNS(SVG_NS, "svg");
        this._svg.style.position = "absolute";
        this._svg.style.left = "0";
        this._svg.style.top = "0";
        this._svg.style.width = "100%";
        this._svg.style.height = "100%";
        container.appendChild(this._svg);

        this._overlayLayer = document.createElement("div");
        this._overlayLayer.style.position = "absolute";
        this._overlayLayer.style.left = "0";
        this._overlayLayer.style.top = "0";
        this._overlayLayer.style.width = "100%";
        this._overlayLayer.style.height = "100%";
        this._overlayLayer.style.pointerEvents = "none";
        container.appendChild(this._overlayLayer);

        this._notice = document.createElement("div");
        this._notice.textContent = "지도 SDK 없이 서버가 계산한 경로만 그린 화면입니다.";
        this._notice.style.cssText = "position:absolute;left:10px;bottom:10px;padding:6px 10px;"
            + "background:rgba(255,255,255,0.9);border:1px solid #ccc;border-radius:4px;font-size:13px;color:#444;";
        container.appendChild(this._notice);

        global.addEventListener("resize", this.invalidate.bind(this));
        this.invalidate();
    }

    /** 라이더를 따라 화면을 옮기지 않는다. 전체 경로가 한눈에 보이는 편이 데모에 낫다. */
    FallbackMap.prototype.setCenter = function (latlng) {
        this._center = latlng;
    };

    FallbackMap.prototype.getCenter = function () {
        return this._center;
    };

    FallbackMap.prototype.invalidate = function () {
        if (this._frame) {
            return;
        }
        var self = this;
        this._frame = global.requestAnimationFrame(function () {
            self._frame = null;
            self._render();
        });
    };

    /** 뷰 범위는 선과 마커로만 잡는다. 움직이는 오버레이까지 넣으면 화면이 계속 흔들린다. */
    FallbackMap.prototype._bounds = function () {
        var minLat = Infinity, maxLat = -Infinity, minLng = Infinity, maxLng = -Infinity;

        function expand(latlng) {
            if (!latlng) {
                return;
            }
            minLat = Math.min(minLat, latlng.getLat());
            maxLat = Math.max(maxLat, latlng.getLat());
            minLng = Math.min(minLng, latlng.getLng());
            maxLng = Math.max(maxLng, latlng.getLng());
        }

        this._polylines.forEach(function (line) {
            line.getPath().forEach(expand);
        });
        this._markers.forEach(function (marker) {
            expand(marker.getPosition());
        });
        expand(this._center);

        if (minLat > maxLat) {
            return null;
        }
        if (maxLat - minLat < MIN_SPAN_DEGREES) {
            var midLat = (maxLat + minLat) / 2;
            minLat = midLat - MIN_SPAN_DEGREES / 2;
            maxLat = midLat + MIN_SPAN_DEGREES / 2;
        }
        if (maxLng - minLng < MIN_SPAN_DEGREES) {
            var midLng = (maxLng + minLng) / 2;
            minLng = midLng - MIN_SPAN_DEGREES / 2;
            maxLng = midLng + MIN_SPAN_DEGREES / 2;
        }
        return {minLat: minLat, maxLat: maxLat, minLng: minLng, maxLng: maxLng};
    };

    /** 경도 1도는 위도 1도보다 짧다. 같은 배율로 그리면 동서 방향이 늘어난다. */
    FallbackMap.prototype._projector = function () {
        var width = this._container.clientWidth || 640;
        var height = this._container.clientHeight || MIN_MAP_HEIGHT;
        var bounds = this._bounds();
        if (!bounds) {
            return null;
        }
        var midLat = (bounds.minLat + bounds.maxLat) / 2;
        var midLng = (bounds.minLng + bounds.maxLng) / 2;
        var lngRatio = Math.cos(midLat * Math.PI / 180);
        var spanX = (bounds.maxLng - bounds.minLng) * lngRatio;
        var spanY = bounds.maxLat - bounds.minLat;
        var usableWidth = width * (1 - VIEW_PADDING_RATIO * 2);
        var usableHeight = height * (1 - VIEW_PADDING_RATIO * 2);
        var scale = Math.min(usableWidth / spanX, usableHeight / spanY);

        return function (latlng) {
            return {
                x: width / 2 + (latlng.getLng() - midLng) * lngRatio * scale,
                y: height / 2 - (latlng.getLat() - midLat) * scale
            };
        };
    };

    FallbackMap.prototype._render = function () {
        var project = this._projector();
        if (!project) {
            return;
        }
        while (this._svg.firstChild) {
            this._svg.removeChild(this._svg.firstChild);
        }

        this._polylines.forEach(function (line) {
            var points = line.getPath().map(function (latlng) {
                var xy = project(latlng);
                return xy.x.toFixed(1) + "," + xy.y.toFixed(1);
            }).join(" ");
            var element = document.createElementNS(SVG_NS, "polyline");
            element.setAttribute("points", points);
            element.setAttribute("fill", "none");
            element.setAttribute("stroke", line.getOptions().strokeColor || "#333333");
            element.setAttribute("stroke-width", line.getOptions().strokeWeight || 5);
            element.setAttribute("stroke-opacity", line.getOptions().strokeOpacity || 0.8);
            element.setAttribute("stroke-linejoin", "round");
            element.setAttribute("stroke-linecap", "round");
            this._svg.appendChild(element);
        }, this);

        this._markers.forEach(function (marker) {
            var xy = project(marker.getPosition());
            var image = marker.getImage();
            var size = image ? image.size : new Size(32, 32);
            var element;
            if (image && image.src) {
                element = document.createElementNS(SVG_NS, "image");
                element.setAttribute("href", image.src);
                element.setAttribute("x", xy.x - size.width / 2);
                element.setAttribute("y", xy.y - size.height / 2);
                element.setAttribute("width", size.width);
                element.setAttribute("height", size.height);
            } else {
                element = document.createElementNS(SVG_NS, "circle");
                element.setAttribute("cx", xy.x);
                element.setAttribute("cy", xy.y);
                element.setAttribute("r", 6);
                element.setAttribute("fill", "#333333");
            }
            this._svg.appendChild(element);
        }, this);

        this._overlays.forEach(function (overlay) {
            overlay.place(project);
        });
    };

    FallbackMap.prototype.addPolyline = function (line) {
        this._polylines.push(line);
        this.invalidate();
    };

    FallbackMap.prototype.removePolyline = function (line) {
        var index = this._polylines.indexOf(line);
        if (index >= 0) {
            this._polylines.splice(index, 1);
            this.invalidate();
        }
    };

    FallbackMap.prototype.addMarker = function (marker) {
        this._markers.push(marker);
        this.invalidate();
    };

    FallbackMap.prototype.removeMarker = function (marker) {
        var index = this._markers.indexOf(marker);
        if (index >= 0) {
            this._markers.splice(index, 1);
            this.invalidate();
        }
    };

    FallbackMap.prototype.addOverlay = function (overlay) {
        this._overlays.push(overlay);
        this._overlayLayer.appendChild(overlay.element());
        this.invalidate();
    };

    FallbackMap.prototype.removeOverlay = function (overlay) {
        var index = this._overlays.indexOf(overlay);
        if (index >= 0) {
            this._overlays.splice(index, 1);
            var element = overlay.element();
            if (element.parentNode === this._overlayLayer) {
                this._overlayLayer.removeChild(element);
            }
        }
    };

    function Marker(options) {
        this._position = options.position;
        this._image = options.image || null;
        this._map = null;
        if (options.map) {
            this.setMap(options.map);
        }
    }

    Marker.prototype.getPosition = function () {
        return this._position;
    };

    Marker.prototype.getImage = function () {
        return this._image;
    };

    Marker.prototype.setMap = function (map) {
        if (this._map) {
            this._map.removeMarker(this);
        }
        this._map = map;
        if (map) {
            map.addMarker(this);
        }
    };

    function Polyline(options) {
        this._options = options || {};
        this._path = this._options.path || [];
        this._map = null;
    }

    Polyline.prototype.getPath = function () {
        return this._path;
    };

    Polyline.prototype.getOptions = function () {
        return this._options;
    };

    Polyline.prototype.setMap = function (map) {
        if (this._map) {
            this._map.removePolyline(this);
        }
        this._map = map;
        if (map) {
            map.addPolyline(this);
        }
    };

    function CustomOverlay(options) {
        this._position = options.position;
        this._xAnchor = options.xAnchor === undefined ? 0.5 : options.xAnchor;
        this._yAnchor = options.yAnchor === undefined ? 1.0 : options.yAnchor;
        this._map = null;

        var content = options.content;
        if (typeof content === "string") {
            var holder = document.createElement("div");
            holder.innerHTML = content;
            content = holder;
        }
        this._content = content;

        this._wrapper = document.createElement("div");
        this._wrapper.style.position = "absolute";
        this._wrapper.style.pointerEvents = "auto";
        this._wrapper.appendChild(content);

        if (options.map) {
            this.setMap(options.map);
        }
    }

    CustomOverlay.prototype.element = function () {
        return this._wrapper;
    };

    CustomOverlay.prototype.getContent = function () {
        return this._content;
    };

    CustomOverlay.prototype.getPosition = function () {
        return this._position;
    };

    CustomOverlay.prototype.setPosition = function (latlng) {
        this._position = latlng;
        if (this._map) {
            this._map.invalidate();
        }
    };

    CustomOverlay.prototype.setMap = function (map) {
        if (this._map) {
            this._map.removeOverlay(this);
        }
        this._map = map;
        if (map) {
            map.addOverlay(this);
        }
    };

    CustomOverlay.prototype.place = function (project) {
        var xy = project(this._position);
        var width = this._wrapper.offsetWidth;
        var height = this._wrapper.offsetHeight;
        this._wrapper.style.left = (xy.x - width * this._xAnchor) + "px";
        this._wrapper.style.top = (xy.y - height * this._yAnchor) + "px";
    };

    function Geocoder() {
    }

    Geocoder.prototype.addressSearch = function (address, callback) {
        DemoGeocoder.locate(address, function (point) {
            if (!point) {
                callback([], Status.ZERO_RESULT);
                return;
            }
            callback([{x: point.x, y: point.y}], Status.OK);
        });
    };

    var Status = {OK: "OK", ZERO_RESULT: "ZERO_RESULT", ERROR: "ERROR"};

    global.kakao = global.kakao || {};
    global.kakao.maps = {
        Map: FallbackMap,
        LatLng: LatLng,
        Marker: Marker,
        MarkerImage: MarkerImage,
        Polyline: Polyline,
        CustomOverlay: CustomOverlay,
        Size: Size,
        Point: PointXY,
        services: {Geocoder: Geocoder, Status: Status},
        load: function (callback) {
            callback();
        }
    };
}(window, document));
