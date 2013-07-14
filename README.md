Volley-demo
===========

An demonstration of Volley - HTTP library announced by google in I/O 2013. 

## Why Volley?

Android has provided two HTTP Clients *AndroidHttpClient* (Extended from apache HTTPClient) and *HttpUrlConnection*
to make a HTTP Request. Both has its own pros and cons. But when an application is developed, we write 

For example, 

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


Key features of the Volley are below.

* Develop Super fast networked applications for android 
* Gives you flexible ways to run your networking requests concurrently with synchronization.
* Comes with inbuilt JSON parsing apis.
* Set prioirty for requests, Retry policy for Timeout, Certain ERROR codes Internal Server error,
* Request cancellation.
* Memory & Disk Caching for images.Batch dispatch to Image Downloads.
* Flexible in giving your own cache implementations,
* You can include your own HTTPStack ( to handle SSL connections, PATCH requests )
* Effective inbuilt cache - control to handle response caching.
* Request tracing for debugging.
* Interface provided is easy to integrate in your UI & network, in the way responses are given back to you.
* 

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

