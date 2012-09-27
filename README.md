GithubTrello
============


Integration tool between Trello and Github.

The goal is to allow you to meld git-flow best practices to the stories setup on a Trello board by automating much of the process.

What it does is to watch for cards who just went out of the backlog into 'in-progress', cards for which it creates a feature branch right from develop. And once that branch is populated with commits, a pull request asking to merge those changes into develop is created too, waiting for enough commits until the merge. 

This java executable will need to be run at regular short intervals (cron, while-sleep bash, …).

###Setup:

Applications tokens for both Trello and Github are will need to be saved in a properties file among other properties.

**Trello**: Login and go [there](https://trello.com/1/authorize?key=d0e4aa36488c2e5957da7c3a61a76ff2&name=Github+Trello&expiration=never&response_type=token&scope=read,write) to accept GithubTrello into your board, you will be given a token to store as `trello.token`

**Github**: Login and go [there](https://github.com/login/oauth/authorize?client_id=2ac660ad9717d1db29b7&scope=repo) to accept GithubTrello into your repository.<br/>
And then, store the `token` field you'll get from the following as `github.token`:
	curl -u $githubUsername https://api.github.com/authorizations

Example for the remaining properties (as in src/main/config/github-trello.properties):

	trello.token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	trello.board-id=xxxxxxxxxxxxxxxxxxxxxxxx
	trello.backlog-list=To Do
	trello.in-progress-list=Doing
	trello.csv.database=snapshots.csv
	github.token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	github.integration-branch=develop
	github.user=user
	github.repo=repo


###Usage:

Java 7 is required, clone the repo and:

	mvn clean package
	chmod +x target/appassembler/bin/github-trello

And launch the following at repeated intervals:
	./target/appassembler/bin/github-trello $pathToPropertiesFile


Warning: If you want to run a build above `mvn package`, append `-DskipTests` or an equivalent, because running the integration tests will make the build fail. Those tests create and delete branches via the Github API on a given repo, which becomes unavailable afterwards, this bug has been notified to Github ([Example here](https://github.com/GithubTrello/test)).