icudi
=========

Description
----------------------------------------------------
my description of the app


Build the dynamic backend
-------------------------

```bash
	cd backend
```
```bash
	mvn install; heroku local
```

view http://localhost:5000

Deploy the dynamic backend
-------------------------

deploy the static frontend

create heroku app

```bash
	cd backend
	heroku create icudi
```

connect heroku app with github
	https://dashboard.heroku.com/apps/icudi/deploy/github

enable automatic deploys

test manual deploy
	click "Deploy Branch" and watch the logs
	wait for "Your app was successfully deployed"

Open your deployed app
	https://icudi.herokuapp.com/systeminfo
