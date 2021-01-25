Plumbr - a memory leak monitoring tool
======================================
Plumbr monitors your Java application, detects if it has any memory leaks 
and reports its findings to you with precise instructions on where the leak
occurs and how bad it is.

1. Getting started
--------------------------------
If you're using Plumbr for the first time, we created a demo application for you
to try out Plumbr, see how it works and what kind of reports it generates.

Please see demo/README.txt file for detailed description of the demo.

2. Installation
--------------------------------
The easiest way to use Plumbr is to double-click on plumbr.jar and select the 
running Java process that you want to connect Plumbr to. Plumbr will pop up 
in your system tray and stay there while your Java application runs.

If you want to make sure Plumbr is always attached to your application, or want 
to use it on the server, do the following:

1. Add the below parameter to your server/application startup command:

-javaagent:/FOLDER_WHERE_YOU_UNZIPPED_PLUMBR/plumbr.jar

2. Start your application and use it as usual. The first thing you will see in 
   your standard out at server start should be the following banner:
     **************************************************
     *                                                *
     * Plumbr (release-version) is started.           *
     *                                                *
     **************************************************
	 
If you have Java 5 or IBM Java, or face problems with installation, please see
our online user manual at http://plumbr.eu/support/manual

3. Usage
--------------------------------
To find a leak, Plumbr needs to analyze the object creation and destruction
patterns within your application. To do so the application has to be used
when Plumbr is attached. This is easiest to achieve in production environment
where your end-users will do the trick, but those who cannot conduct leak
hunting in production environments should simulate the application usage.

There are numerous ways to simulate the end-user behaviour -- for example to
generate load on a web application we can recommend Apache JMeter to generate
load on your application. Whatever is the tool of your choice - bear in mind
that Plumbr needs information from different parts of your application to
determine what is "normal" and what is "suspicious" behaviour.

The time it takes Plumbr to discover a leak is application-dependent and can
vary from just minutes to days. The best way to keep an eye on what Plumbr is
doing and also browse your existing leak reports is to use MyPlumbr:
https://portal.plumbr.eu/

If you are behind the firewall you cannot control, you may use JMX interface to
monitor Plumbr status: http://plumbr.eu/support/troubleshooting/jmx-api

Need help?
-------------------------------
Contact us at Plumbr forum at http://www.plumbr.eu/support/forum
or write us: support@plumbr.eu