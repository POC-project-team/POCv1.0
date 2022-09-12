
# Getting started
The app is running in docker container

## Running the app (docker required)
Ports needed:
	60494
	can change in config file
```
cd <navigation_to_project_root>/Backend

docker build -t <container name> .; docker run -tid <container name>
or via docker-compose 
docker-compose up --build -d
```
