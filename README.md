Volley-demo
===========

An demonstration of Volley - HTTP library announced by google in I/O 2013. 

## Why Volley?

Android has provided two HTTP Clients *AndroidHttpClient* (Extended from apache HTTPClient) and *HttpUrlConnection*
to make a HTTP Request. Both has its own pros and cons. When an application is developed, we write HTTP connection classes which handles
all the HTTP requests, creating THREADS to run in background, managing THREAD pool, response parsing, response caching, handling error codes, SSL connections, running requests in parallel and others stuffs around that.
Every developer has his own way of implementing these functionalities.Some might use AsycnTask, some might use passing handlers created from UI thread to HTTP connection classes which then uses the handler to pass back the parsed HTTP response back to the main thread.

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
implementing the business logic once the response is obtained.

*NOTE* Volley is not good for larger file download/upload operations as well video streaming operations. 

Key features of the Volley are below

* Develop Super fast networked applications for android.
* Gives you flexible ways to run your networking requests concurrently with synchronization.
* Comes with inbuilt JSON parsing.
* Set prioirty for requests.
* Retry policy for timeout,certain ERROR codes as Internal Server error.
* Request cancellation.
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

