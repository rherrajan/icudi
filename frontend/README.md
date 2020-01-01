icudi
=========

Description
----------------------------------------------------
my description of the app


Build the static frontend
-------------------------
```bash
	cd frontend
```

generate the html

```bash
	npm start
```

show the html

```bash
	http-server ./dist &
```

optional: promote coming changes via grunt

```bash
	grunt watch
```


view http://localhost:8080

Deploy the static frontend
-------------------------

create a new [github repository](https://github.com/new) and push your code to it

```bash
	git remote add origin https://github.com/<accountname>/icudi.git
	git push -u origin master
```

create a new [netlify app](https://app.netlify.com/start) connecting to github

open "domain settings"-> "Custom Domains" -> "edit site name"
change the generated site name to "icudi"

open https://icudi.netlify.com
