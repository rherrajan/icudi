icudi
=========

Description
----------------------------------------------------
my description of the app

Prerequisites
-------------------------
```bash
sudo apt install -y git maven

sudo apt install -y build-essential libssl-dev
curl https://raw.githubusercontent.com/creationix/nvm/v0.34.0/install.sh | bash
source ~/.profile
nvm --version

nvm install 5.0
nvm use 5.0
npm install -g grunt-cli
npm install grunt-metalsmith --save-dev
npm install grunt-contrib-watch --save-dev
npm install -g http-server

sudo apt install -y snapd 
export PATH=/snap/bin:$PATH
sudo snap install --classic heroku

```

Commit the generated files
-------------------------
```bash
	git init
	git add -A
	git commit -m"initial setup via clanneyder"
```

SetUp remote database
-------------------------
```bash
	heroku create icudi
	heroku addons:create heroku-postgresql
```

SetUp local database
-------------------------
```bash
	
	sudo apt install -y postgresql postgresql-contrib pgadmin3
	
	#sudo -u postgres dropuser heroku
	#sudo -u postgres dropdb herokutest

	sudo -u postgres createuser -P -d heroku
	sudo -u postgres createdb -O heroku heroku
	
	sudo xed /etc/postgresql/10/main/postgresql.conf 
	sudo /etc/init.d/postgresql restart
	
	export JDBC_DATABASE_URL=jdbc:postgres://heroku:password@localhost:5432/heroku
	# >> ~/.bashrc
	# >> /etc/environment (to introduce the variable to test in ide for some setups)
	
```

Build the project
-------------------------
see documentation [for frontend](frontend/README.md) and [for backend](backend/README.md)


Deploy the project
-------------------------
see documentation [for frontend](frontend/README.md) and [for backend](backend/README.md)


WIP
-------------------------

github account
git add --all; git commit -m"."; git push -u origin master
https://github.com/rherrajan/twoTier

netlify account
https://two-tier.netlify.com/
npm start

heroku account
https://two-tier.herokuapp.com/
mvn install; heroku local
heroku logs --tail --app two-tier

grunt
nvm install 5.0
nvm use 5.0
npm install -g grunt-cli
npm install grunt-metalsmith --save-dev
npm install grunt-contrib-watch --save-dev

grunt watch

http-server dist


Credits
-------------------------
build with [Clanneyder](https://github.com/rherrajan/clanneyder)

