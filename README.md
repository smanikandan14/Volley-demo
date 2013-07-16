Volley-demo
===========

An demonstration of Volley - HTTP library announced by google in I/O 2013. 

## Why Volley?

Android has provided two HTTP Clients *AndroidHttpClient* (Extended from apache HTTPClient) and *HttpUrlConnection*
to make a HTTP Request. Both has its own pros and cons. When an application is developed, we write HTTP connection classes which handles
all the HTTP requests, creating THREADS to run in background, managing THREAD pool, response parsing, response caching, handling error codes, SSL connections, running requests in parallel and others stuffs around that.
Every developer has his own way of implementing these functionalities.Some might use AsycnTask for running network operations in background, or some might use passing handlers created from UI thread to HTTP connection classes which then executes network operation in worker thread and uses the handler to pass back the parsed HTTP response back to the main thread.

But we end up writing same boilerplate codes repeatedly and we try to reinvent the wheel in our application development.

For example, in the below snippet, a HTTP request is made in the AysncTask's *doBackground* method. When the response is obtained,
data is copied from HttpUrlConnection's *InputStream* to *OutputStream* and then it tries to convert the string obtained from 
outputStream to *JSONObject* which is our final response. On the course, all the necessary try, catch block is handled.
All these boilerplate codes are repeated throughout our code. 

```
HttpURLConnection urlConnection = null;
try {
   URL url = new URL("http://www.android.com/");
   urlConnection = (HttpURLConnection) url.openConnection();
   InputStream in = new BufferedInputStream(urlConnection.getInputStream());
   ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
   byte[] buffer = new byte[1024]; // Adjust if you want
   int bytesRead;
   while ((bytesRead = in.read(buffer)) != -1) {
     outputStream.write(buffer, 0, bytesRead);
   }
   JSONObject resultJSON = new JSONObject(outputStream.toString());
   
}catch (Exception e) {
	   e.printStackTrace();
} finally {
     urlConnection.disconnect();
}
```

Google has come up with Volley interface which helps developers to handle all the network related operations so that developers can concentrate
implementing the business logic after the HTTP response is obtained.Also having less code for network calls helps developer reduce number of bugs.

*NOTE* Volley is not good for larger file download/upload operations as well video streaming operations. 

Key features of the Volley are below

* Develop Super fast networked applications for android.
* Schedules all your HTTP requests running them parallely in background threads and manages those threads.
* Gives you flexible ways to run your networking requests concurrently with synchronization.
* Comes with inbuilt JSON parsing the response.
* Set prioirty for requests.
* Retry policy for timeout,certain ERROR codes as Internal Server error.
* Flexible Request cancellations.
* Memory & Disk Caching for images.Batch dispatch to Image Downloads.
* Flexible in giving your own cache implementations.
* You can include your own HTTPStack ( to handle SSL connections, PATCH requests ).
* Effective inbuilt cache - control to handle response caching.
* Request tracing for debugging.
* Excels in the way responses are given back to you.


## Integrating Volley to your project.

You can include in two ways
* Create *Volley.jar* and include as *jar dependency* to your project.
* Include the volley project as *Library Dependency* in your project.

Clone the Volley project from below git repo.
    https://android.googlesource.com/platform/frameworks/volley/)

1. Creating Volley.jar  
    * Import the project into eclipse.  
    * $ cd volley  
    * $ android update project -p .  (Generate local.properties file )  
    * $ ant jar  
    * Right click on build.xml file and ‘Run as Ant Task’ , volley.jar would be created in /bin folder.  

2. Library Dependency  
    * Edit the project.properties file and the add the below line  
    * android.library=true  
    * Now right click on your project--> Properties--> Android --> Under Library section, choose ‘Add’ and select ‘Volley’ project as library dependency to your project.

Using Volley involves two main classes **RequestQueue** and **Request**.
* RequestQueue - Dispatch Queue which takes a Request and executes in a worker thread or if cache found its takes from cache and responds back to the UI main thread.
* Request - All network(HTTP) requests are created from this class. It takes main parameters required for a HTTP request like
	* METHOD Type - GET, POST, PUT, DELETE
	* URL 
	* Request data (HTTP Body)
	* Successful Response Listener
	* Error Listener
Volley Provides two specific implementations of **Request**.
	* JsonObjectRequest
	* StringRequest

## Initialise RequestQueue

```
mVolleyQueue = Volley.newRequestQueue(this);
```

You can create a instance of RequestQueue by passing any *Object* to the static method *newRequestQueue* of Volley Class. 
In this case, activity instance *this* is passed to create the instance. Similarly while cancelling all the requests dispatched
in this RequestQueue we should be using activity instance to cancel the requests.

## JsonObjectRequest
Creates HTTP request which helps in connecting to JSON based response API's. Takes parameters as   

* HTTP METHOD 
* A JSON object as request body ( mostly for POST & PUT api's)
* Success & Error Listener.

Volley parses the HTTP Response to a JSONObject for you. If your server api's are JSON based, you can straightway go ahead and use *JsonObjectRequest*

> The **Content-Type** for this request is always set to *application/json*

```
String url = "<SERVER_API>";

JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
							url, null, 
							new Response.Listener<JSONObject>() {
	@Override
	public void onResponse(JSONObject response) {
	}
}, new Response.ErrorListener() {

	@Override
	public void onErrorResponse(VolleyError error) {
	}
});

mVolleyQueue.add(jsonObjRequest);

```
## StringRequest
To obtain the HTTP Response as a String, create HTTP Request using *StringRequest*
Takes parameters as   

* HTTP METHOD 
* Success & Error Listener.

```
String url = "<SERVER-API>";

StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
	@Override
	public void onResponse(String response) {
	}
}, new Response.ErrorListener() {
	@Override
	public void onErrorResponse(VolleyError error) {
	}
});

mVolleyQueue.add(stringRequest);
```

## GsonRequest
You can customize the *Request<T>* to make a new type of Request which can give the response in Java Class Object.
GSON is a library which is used in converting JSON to Java Class Objects and vice-versa. You write custom request which takes Java Class name as a parameter and return the response in that Class Object.

You should include **gson.jar** as JAR dependency in your project. 

```
public class GsonRequest<T> extends Request<T>{
	private Gson mGson;
	
	public GsonRequest(int method, String url, Class<T> cls, String requestBody, Listener<T> listener,
	    ErrorListener errorListener) {
		super(method, url, errorListener);
		mGson = new Gson();        
	}
	        
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
		    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		    	T parsedGSON = mGson.fromJson(jsonString, mJavaClass);
		    return Response.success(parsedGSON,
		            HttpHeaderParser.parseCacheHeaders(response));
		    
		} catch (UnsupportedEncodingException e) {
		    return Response.error(new ParseError(e));
		} catch (JsonSyntaxException je) {
		    return Response.error(new ParseError(je));
		}
	}
}        
```

Using GsonRequest 
* Takes *FlickrResponsePhotos* class as parameter and returns the response as *FlickrResponsePhotos* Object.
* Makes life easier for developers if you have the response in your desired Class objects.

```
gsonObjRequest = new GsonRequest<FlickrResponsePhotos>(Request.Method.GET, url,
		FlickrResponsePhotos.class, null, new Response.Listener<FlickrResponsePhotos>() {
	@Override
	public void onResponse(FlickrResponsePhotos response) {
	}
}, new Response.ErrorListener() {

	@Override
	public void onErrorResponse(VolleyError error) {
	}
});
mVolleyQueue.add(gsonObjRequest);
```

## Image Download
Most common operation in an application is *Image* download operation.

* ImageRequest
* NetworkImageView
* ImageDownloader

## SSL connections

## Adding Headers
By default Volley adds *Content-Type* parameter in all Request Header.
* JsonRequest --> *application/json*
* StringRequest --> *application/x-www-form-urlencoded*
* Request<T> --> *application/x-www-form-urlencoded*
* 
If you wanted to change this behavior, you need to override

```
public String getBodyContentType() {
	return "application/x-www-form-urlencoded; charset=UTF-8";
}
```

To add additional headers to the requests, you need to extend from the existing Requests type and implement the *getHeaders()* method

```
public class MyStringRequest extends StringRequest{
	private Map<String, String> headers = new HashMap<String, String>();
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers;
	}
	
	public void setHeader(String title, String content) {
		headers.put(title, content);
	}
}
```

## Handling Error Codes


## Request Cancellation
You can cancel a single request or multiple requests or cancel requests under a TAG name. Using any of one approach.

```
request.cancel();

( or )

for( Request<T> req : mRequestList) {
	req.cancel();
}

( or )

volleyQueue.cancelAll(Object);
```

You can probably store all the requests made in a screen into a List and cancel the requests one by one iterating the list.
Or Cancel all the requests made by using *VolleyQueue* instance. 

You should (must) do cancelling all the requests made in activitie's **onStop()** method. ( Except where you want a POST/PUT request to continue though user leaves the screen).

## Set PRIORITY to Requests
You can set Priority to your requests. Normally *ImageRequests* are assigned *LOW* priority and other requests like *JsonObjectRequest* and *StringObjectRequest* are set to *NORMAL* priority.
To change the priority for different server requests for your needs you should customize the *Request* class and override *setPriority* and *getPriority* methods.
```
Priority priority;
public void setPriority(Priority priority) {
	this.priority = priority;
}

/*
 * If prioirty set use it,else returned NORMAL
 * @see com.android.volley.Request#getPriority()
 */
public Priority getPriority() {
    if( this.priority != null) {
    	return priority;
    } else {
    	return Priority.NORMAL;	
    }
}	

```

## Retry Policy

## Response Caching 
Enable response caching to quickly fetch the response from cache, if below api is set to true.

```
request.setShouldCache(true);
```
* Handling response headers - Cache-Control
Volley decides whether to cache the response or not, based on response headers obtained. Some of the parameters
it looks for are *Cache-control, maxAge, Expires*.

* In demo of stringObjectRequest it uses weather api, the response headers has **Cache-Control: no-cache, must-revalidate**. In this case, even if 
*setShouldCache()* api is set true for the request, Volley decides not to store the response, because server has sent response headers as *must-revalidate*
So storing response doesn't make sense for this api. Some of these intelligences are implemented already in Volley, you need not take the burden of
parsing response headers for especially for caching.

```
Sample Response headers for different requests.

curl -i http://api.openweathermap.org/data/2.5/weather?q=London,uk
HTTP/1.1 200 OK
Cache-Control: no-cache, must-revalidate
Content-Type: application/json; charset=utf-8
Date: Mon, 15 Jul 2013 08:10:31 GMT
Pragma: no-cache
Server: nginx
X-Powered-By: OWM
Content-Length: 402
Connection: keep-alive

curl -i http://farm4.static.flickr.com/3792/9109500182_a2721e9a32_t.jpg
HTTP/1.1 200 OK
Date: Sun, 23 Jun 2013 14:32:24 GMT
Content-Type: image/jpeg
Content-Length: 3253
Accept-Ranges: bytes
Cache-Control: max-age=315360000,public
Expires: Fri, 23 Jun 2023 02:09:58 UTC

```

## Enablind DEBUG Logs on adb logcat for Volley
* $adb shell
* $setprop log.tag.Volley VERBOSE
* logcat

Now you can see Volley debug logs shown in terminal. You can test it by launching **Play Store** app which uses Volley.

```
D/Volley  (24482): [1] MarkerLog.finish: (7067 ms) [X] http://api.flickr.com/services/rest?api_key=5e045abd4baba4bbcd866e1864ca9d7b&method=flickr.interestingness.getList&format=json&nojsoncallback=1 0x86cad52e NORMAL 1
D/Volley  (24482): [1] MarkerLog.finish: (+0   ) [ 1] add-to-queue
D/Volley  (24482): [1] MarkerLog.finish: (+6   ) [9624] cache-queue-take
D/Volley  (24482): [1] MarkerLog.finish: (+7   ) [9624] cache-hit-expired
D/Volley  (24482): [1] MarkerLog.finish: (+0   ) [9625] network-queue-take
D/Volley  (24482): [1] MarkerLog.finish: (+5726) [9625] network-http-complete
D/Volley  (24482): [1] MarkerLog.finish: (+1158) [9625] network-parse-complete
D/Volley  (24482): [1] MarkerLog.finish: (+169 ) [9625] network-cache-written
D/Volley  (24482): [1] MarkerLog.finish: (+1   ) [9625] post-response
**D/Volley  (24482): [1] MarkerLog.finish: (+0   ) [ 1] canceled-at-delivery**

```

The above debug logs shows that response is ignored since user has called cancelled for the request. Volley has decided to ignore the response
before parsing the response avoiding wasteful CPU cycles.

## Credits
* http://howrobotswork.wordpress.com/2013/06/02/downloading-a-bitmap-asynchronously-with-volley-example/
* http://bon-app-etit.blogspot.in/2013/04/the-dark-side-of-asynctask.html
* http://www.checkupdown.com/status/E304.html


##License
```
 * Copyright 2013 Mani Selvaraj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
```


