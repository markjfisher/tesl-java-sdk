Add following to ~/.docker/config.json

    "credsStore": "secretservice",

Now every login will be saved to the system password and keys 

Also needed to add a section to the credHelpers for each service:

    "credHelpers": {
            "docker.pkg.github.com": "secretservice",
            "https://index.docker.io/v1/": "secretservice",


### DOCKER HUB

Login to docker hub

    docker login --username=markjfisher

for a password, use a token generated on the docker hub site.

### GITHUB

Get a token for your github user, follow https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line

Login to docker github:

    docker login docker.pkg.github.com -u <github user> -p <token>

This will store a value in the local keychain that can be seen using "Passwords and keys" in Linux.

You can also view the details with the application `secret-tool`:

    secret-tool lookup username <github user>

The path of the image has to be correct for github, my example is:

    image = "docker.pkg.github.com/markjfisher/tesl-java-sdk/tesl-java-rest-repo"

Note the format is `docker.pkg.github.com/<github user>/<project name>/<repo name>`
