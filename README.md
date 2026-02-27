I'm using the README to just document my process here to get this app working.

To start, it looks like Spring has a quickstart initializer so I ran that with what looks like the latest stable release 4.0.3 and it generated me a zipfile that has the standard gradle setup including some dependencies and a latest version of java, which is 25.  One hiccup I ran into was that IDEA wasn't recognizing the installed JDK so I had to install one to my local home.  I suspect that this was because my IDEA is a flatpak install (my NetBeans had the same issue) but even when I used flatseal to extend permissions to /usr/lib/jdk it still didn't see the install dir, so I let that go for now since I needed to make progress on the rest.

At home I use Intellij so after unzipping the prebuilt project from the spring initializer, I opened the resulting project there and the gradle build script ran fine, so success there. After that I had to add a main application so I could start the server using gradle.  Adding this run step also let me run the server from inside the IDE so I could debug successfully. Here's where I saw the spring boot banner in the IDE and that verified I had tomcat running.

Next, I needed a main rest controller, so built a package “controllers” under com.gaggledemo and added a class RestController which would hold my endpoints.  For specialized endpoint groupings, I'd want more than just one class but for now, we can go with just one.

I did notice though that the default project didn't contain a critical import in the gradle.build, so I added 

implementation 'org.springframework.boot:spring-boot-starter-web'

And this allowed importing of @RestController and @GetMapping.  Since we were going to be using a DB, I chose to use SQLLite so I also added 

implementation 'org.xerial:sqlite-jdbc:3.51.2.0'

from the maven repo as my persistence choice.  I haven't used this DB before and for something a bit more larger scale, I'd probably go with Postgres just because I know it reasonably well and it's free, but SQLLite has an in-memory option and is used by a bunch of very highly rated apps (think web browsers and such) so it's going to be fine; besides jdbc makes persistence choices fairly flexible.  I also set up a /ping GET endpoint and after spinning up tomcat, I was able to hit this endpoint successfully using Bruno (kind of like Postman but I like it better)

At this point, since I had some workable code, it was time to set up git for a commit.  I already have a github account set up so I just created a new repo there named gaggledemo.  I've enabled the local project with git and was able to set up authentication between github and my IDE as well as command-line git using an expiring token and have tested all of that and everything has committed just fine.  I want to set git up with both IDEA and command-line because sometimes you need the flexibility of the command line to sort out when git misbehaves.

So, going back to the document, the next thing is to create some of the DDL for the DB, creating both tables.  I'm looking now more closely at sqllite and it's in-memory capability and according to the docs, even if it's in-memory, multiple connections can share the same in-memory DB if we use file::memory:?cache=shared structure in the jdbc url.  but, to test this, I think we can put together a little unit test and start to build out our test cases as well.

update: so SQLLite failed my across-connection test.  I've written the test and it did detect the failure however I could not get a jdbc url with the proper cross-connection sharing behavior.  Luckily I was able to just switch my dependency to the H2 db, update the jdbc URL to match the H2 format for cross-connection sharing and the test worked fine.  I'm not sure still how H2 fares with multiple connections going on at once but I think for now it should be ok.  Next I want to create a SQL file which contains the database structure - this will run before spring-boot init so any other stuff it needs to do will have the database support.

Next, I've created a startup SQL file to hold the schema to run before bootup, just to populate the empty DB and have also created a SQL Util class to help with this in some way.  The SQL file will just consist of statements separated by semi-colons and the parsing method in the util class will help with splitting that up since often, feeding in a file like this raw to jdbc will make it choke.  I'll also probably introduce comments in the file maybe with a single line // just so we can do some documentation on that file later on

Update: so it looks like a convenient way to document my progress is just to do a paragraph entry for each milestone like a diary, so that's what I'll do.  I've now created that schema file and put it into resources since it will need to be bundled with the app when it ships.  I've also introduced a SQLUtil app with corresponding tests so sql files can be parsed and will support // type commenting.  The tests pass so I think we're good to go.  Next step is to write a simple test that will create a throwaway DB and just run the schema to prevent any fatfingered SQL from getting into production.  At that point, I think we can do another git commit

I've now introduced more capability to SQLUtil which allows us to generate connections and run a list of SQL statements.  Also discovered a bug in the parser which let blank lines through so corrected that.  And as a result of the new unit test, found that the USER table is reserved so changed that table name to STUDENT.  But so far, I'm definitely liking H2, it's pretty easy to work with.  One thing I need to think about next is connection management and logging.  Based on how we already have a Util class to deal with connectivity, we may not need to use connection pooling yet but if we have time, that could come.

Another quick update: turns out that we've got logback bundled in with the spring boot so it was quick work to add some logging.  Running a few tests in the IDE confirmed that it was working fine.  I've used a nice construction in the LoggerFactory to pull in the dynamic class name via MethodHandles.  I can't forget the foreign key constraints in the startup.sql file, though.  That's probably next, then we can look at the ORM stuff I think.  

Ok, it's the next day and I'm now looking at the data layer.  I've created some named foreign key constraints on createdBy and editedBy into the student table and have run the associated test to ensure that the syntax was fine. Am looking at JPA next.


