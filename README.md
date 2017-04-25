[ Date : 2017. 03. 06(월) ]
						--------------- Today's Topic ------------------
								(1) 웹서버 통신으로 공공데이터 사용하기
								(2) 주요 코드
						------------------------------------------------

 - 프로젝트명 : [Parking]
 
 - 내용 : 서울시에서 제공하는 "서울시 실시간 주차장 정보" API를 사용하여 애플리케이션의 구글맵에 표시합니다.

#1. 웹서버 통신으로 공공데이터 사용하기

## 출력화면

![](http://i.imgur.com/pgTdJEj.png)

##1.1 API 가져오기 

--> 서울시 공공데이터 홈페이지에서 먼저 openAPI 키를 가져와야 됩니다.

![](http://i.imgur.com/DbGCCkB.png)

![](http://i.imgur.com/YHrhvhs.png)

// 인증키 받아오기 성공!!!


##1.2 Convert json to class

--> 주차장 정보 데이터를 테이블로 생성하기 위해 직접 코딩하지 않고 자동으로 변환해주는 사이트를 이용하겠습니다.

###1.2.1 json 파일

아래 링크는 "서울 시내 주차장" 정보를 실시간으로 조회할 수 있는 샘플 URL 입니다.

		http://openapi.seoul.go.kr:8088/(인증키)/xml/SearchParkingInfoRealtime/1/5/

위 (인증키)란에 1.1에서 받은 본인의 인증키를 넣어 사용하면 됩니다.

###1.2.2 CodeBeauty

CodeBeauty는 사용자의 가독성을 고려하고 클래스 파일 변환을 위해 아래 그림처럼 json 파일을 객체 hierarchy에 맞게 변환시켜줍니다. 

![](http://i.imgur.com/tIiYkPe.png)

###1.2.3 Pojo classes

Pojo classses 사이트는 json 파일을 객체로 변환시켜 줍니다. 안드로이드 프로젝트에서 이 객체를 사용합니다.

![](http://i.imgur.com/MqlRzw0.png)

###1.2.4 안드로이드 Park.java

Pojo classes에서 만든 json 객체로 안드로이드에서 주차장 정보를 담을 테이블을 만듭니다. 

![](http://i.imgur.com/go8m6qy.png)

----------------------------------------------

#2. 주요 코드


##2.1 Remote 클래스

<메소드>

 - getData() : 백그라운드에서 웹서버에 있는 데이터(서울시 실시간 주차장 정보)를 받습니다. 
 

 - Callback interface : MapsActivity와 Remote 클래스간의 통신입니다. 인터페이스에 있는 메소드들을 MapsActivity에서 정의하고 Remote 클래스에서 사용합니다.


###2.1.1 getData()

![](http://i.imgur.com/YuQsnXZ.png)
![](http://i.imgur.com/jlAVkoC.png)
![](http://i.imgur.com/2SFpj38.png)

(1) week8-4의 HttpUrlConnection 프로젝트에서 사용한 동일한 코드로써 1장에서 다룬 url 주소(서울 시내 주차장 정보)로 가서 데이터를 가져옵니다.

(2) Remote 객체를 생성한 측(MapsActivity.java)으로부터 시스템 자원을 받아서 프로그래스바를 생성합니다.

                obj.getProgress().setProgressStyle(ProgressDialog.STYLE_SPINNER);
                obj.getProgress().setMessage("불러오는 중........");
                obj.getProgress().show();

(3) Remote 객체를 생성한 측의 callback 함수( "public void call(String jsonString)" )를 호출하고 웹서버에서 받은 데이터(result)를 넘깁니다. 

				obj.call(result);


###2.1.2 Callback interface

MapsActivity와의 통신을 위해 사용되는 메소드들을 선언합니다. 
					
					    interface Callback{
					        public Context getContext();				// Remote 객체를 생성한 측의 context를 받아서 시스템 자원을 사용합니다.
					        public String getUrl();						// 웹서버에 요청할 url 주소를 반환합니다. 
					        public void call(String jsonString);		// 웹서버로부터 받은 데이터를 가지고 필요한 정보만을 구글맵에 표시합니다.
					        public ProgressDialog getProgress(); 		//프로그레스바를 설정합니다.
					    }

##2.2 MapsActivity 클래스

Json String을 Json Object로 변환하여 필요한 정보(주차장 빈공간, 전체 공간)만을 가져왔습니다. 이 정보는 구글맵에 마커로 표시합니다.

###2.2.1 onMapReady()

[MapsActivity.java]

![](http://i.imgur.com/M2GbE9C.png) 

앱이 실행되고 onCreate()에서 구글맵이 사용될 준비가 끝나면 호출되는 메소드입니다.

(1) GoogleMap과 프로그레스바 API를 초기화합니다.

    				    mMap = googleMap;
   				        dialog = new ProgressDialog(this);

(2) Remote 객체를 생성합니다. getData()를 호출하여 url 주소에서 데이터를 string(json string)으로 가져옵니다.
	
						        Remote remote = new Remote();
      						    remote.getData(this);

(3) 애플리케이션이 실행되면 처음에 서울 장면을 보여줍니다.        

		 LatLng seoul = new LatLng(37.566696, 126.977942);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10));


###2.2.2 call()

콜백으로서, Remote 클래스에서 호출되는 함수입니다. json string을 json 객체로 바꾼다음 필요한 데이터를 가져와서 맵에 마커로 표시합니다.

[MapsActivity.java]

![](http://i.imgur.com/vYYU3oc.png)
![](http://i.imgur.com/NcZ01fc.png)

(1) json String 전체를 JSONObject로 변환시킵니다.

								JSONObject jsonObject = new JSONObject(jsonString);

(2) JSONObject 중에 최상위의 object를 꺼낸다(json 파일에서 root 태그를 객체로 꺼냅니다)

						JSONObject rootObject = jsonObject.getJSONObject("SearchParkingInfoRealtime");

(3) 사용하려는 주차장 정보들을 JSONArray로 꺼낸다.

						JSONArray rows = rootObject.getJSONArray("row");


(4) JSONArray에 담긴 "row" 데이터들을 for-loop로 하나씩 꺼내고 park(참조변수)가 가리키게 합니다.


						JSONObject park = rows.getJSONObject(i);

(5) 주차장 정보 중에는 중복되는 데이터가 많습니다. 중복되는 데이터를 방지하기 위해 "PARKCING_CODE"로 구별합니다.

                
				List<String> parkCode = new ArrayList<>();
                code = park.getString("PARKING_CODE");
                if(parkCode.contains(code)){
                    continue;
                }
				parkCode.add(code);

(6) 위도/경도로 해당 좌표를 맵에 표시합니다. 이때 주차장 전체공간("CAPACITY")과 현재 주차장이 점유된 공간("CUR_PARKING")도 같이 표시합니다.


				double lat = getDouble(park, "LAT");
                double lng = getDouble(park, "LNG");
                LatLng parking = new LatLng(lat, lng);

                int capacity = getInt(park, "CAPACITY");
                int current = getInt(park,"CUR_PARKING");
                int space = capacity - current;
                mMap.addMarker(new MarkerOptions().position(parking).title(space + "/" + capacity)).showInfoWindow();			

###2.2.3 getDouble(), getInt()

--> 2.2.2 call()메소드 안에 사용되는 함수들입니다. JSON 객체에서 필요한 데이터 꺼낼 때, 데이터가 아무것도 없을 경우를 위해 exception 처리를 해둡니다. 



				    private double getDouble(JSONObject obj, String key){
				        double result = 0;
				        try {
				            result = obj.getDouble(key);
				        }catch(Exception e){
				
				        }
				
				        return result;
				    }
				
				    private int getInt(JSONObject obj, String key){
				        int result = 0;
				        try {
				            result = obj.getInt(key);
				        }catch(Exception e){
				
				        }
				
				        return result;
				    }

##2.3 Park 클래스

Park 클래스는 이 프로젝트에서 직접적으로 사용하지 않았습니다.

Json String을 자바 Object (Json Object가 아니라)로 사용하기 위한 클래스입니다.

"1.2.3"장에 소개하였습니다. Pojo classes 홈페이지에서 Json String을 클래스문으로 변환시킵니다. 변환된 클래스문을 안드로이드 프로젝트에서 새로운 클래스를 생성해서 복사, 붙이기 하였습니다.