I'm using the README to just document my process here to get this app working.

To start, it looks like Spring has a quickstart initializer so I ran that with what looks like the latest stable release 4.0.3 and it generated me a zipfile that has the standard gradle setup including some dependencies and a latest version of java, which is 25.  One hiccup I ran into was that IDEA wasn't recognizing the installed JDK so I had to install one to my local home.  I suspect that this was because my IDEA is a flatpak install (my NetBeans had the same issue) but even when I used flatseal to extend permissions to /usr/lib/jdk it still didn't see the install dir, so I let that go for now since I needed to make progress on the rest.

At home I use Intellij so after unzipping the prebuilt project from the spring initializer, I opened the resulting project there and the gradle build script ran fine, so success there. After that I had to add a main application so I could start the server using gradle.  Adding this run step also let me run the server from inside the IDE so I could debug successfully. Here's where I saw the spring boot banner in the IDE and that verified I had tomcat running.

Next, I needed a main rest controller, so built a package “controllers” under com.gaggledemo and added a class RestController which would hold my endpoints.  For specialized endpoint groupings, I'd want more than just one class but for now, we can go with just one.

I did notice though that the default project didn't contain a critical import in the gradle.build, so I added 

implementation 'org.springframework.boot:spring-boot-starter-web'

And this allowed importing of @RestController and @GetMapping.  Since we were going to be using a DB, I chose to use SQLLite so I also added 

implementation 'org.xerial:sqlite-jdbc:3.51.2.0'

from the maven repo as my persistence choice.  I haven't used this DB before and for something a bit more larger scale, I'd probably go with Postgres just because I know it reasonably well and it's free, but SQLLite has an in-memory option and is used by a bunch of very highly rated apps (think web browsers and such) so it's going to be fine; besides jdbc makes persistence choices fairly flexible.  I also set up a /ping GET endpoint and after spinning up tomcat, I was able to hit this endpoint successfully using Bruno (kind of like Postman but I like it better)

At this point, since I had some workable code, it was time to set up git for a commit.  I already have a github account set up so I just created a new repo there named gaggledemo.

