# NoSQL with Spring, Hibernate, and PostgreSQL's hstore


## Run Locally

Setup your PostgreSQL database to support hstore by opening a `psql` connection to it:

    $ psql -U username -W -h localhost database

Then enable hstore:

    => create extension hstore;
    => \q

Build the app:

    $ mvn package

Set the `DATABASE_URL` environment variable to point to your PostgreSQL server:

    $ export DATABASE_URL=postgres://username:password@localhost/databasename

Start the app:

    $ java -cp target/classes:target/dependency/* com.jamesward.Webapp

[Try it out](http://localhost:8080)

## Run on Heroku 

[Install the Heroku Toolbelt](http://toolbelt.heroku.com)

Login to Heroku:

    $ heroku login

Create a new app:

    $ heroku create

Add Heroku Postgres:

    $ heroku addons:add heroku-postgresql:dev

Tell Heroku to set the `DATABASE_URL` environment variable based on the database that was just added (replace `YOUR_HEROKU_POSTGRESQL_COLOR_URL` with your own):

    $ heroku pg:promote YOUR_HEROKU_POSTGRESQL_COLOR_URL

Open a `psql` connection to the database:

    $ heroku pg:psql

Enable hstore support in your database:

    => create extension hstore;
    => \q

Deploy the app:

    $ git push heroku master

View the app on the cloud:

    $ heroku open
