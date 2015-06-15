# KnowledgeWorks
Knowledge works web for GDM lab at Fudan University.

Please contact us for any problems.

===Requirement===

1. Eclipse Java EE IDE for Web Developers.

2. Apache Maven.

3. Tomcat v7.0 Server.

4. Java version depends on Tomcat (GDM server is 1.7).

5. Spring framework.

===Configuration===

1. Clone to Eclipse workspace.

2. Wait for Maven downloading and auto configuration.

3. If your Eclipse is EE IDE, skip this step. Project->Properties->Java Compile Path->Add JAR->Select WebContent/WEB-INF/lib/cnell1.7.jars.

4. Run on Tomcat v7.0 Server.

===Introduction===

1. We configure a Spring framework, thus all projects on KnowledgeWorks has its own controller and web pages.

===Tips===

1. The Tomcat in GDM is configured with Chinese encode feature, therefore the input word from web is coded in UTF-8. But for local server, for example on your desktop, the string should be transformed to UTF-8. The search function in src/controller/CNDBController.java contains a try{}catch{} component, where I comments it. 