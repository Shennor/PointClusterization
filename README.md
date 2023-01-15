# Concurrent Decentralized Systems
## K-means clustering

This repository contains a coursework project that implements a concurrent decentralized system for solving the k-means clustering problem. The system consists of two main parts:

The main backend, or "master," which is able to receive tasks with points via the /solve endpoint.
The calculation node, or "slave," which connects to the master via WebSocket and waits for tasks.
The project includes Docker files for both the master and slave, as well as a docker-compose file in the root of the repository that can be used to spin up the system with one master and multiple slaves.

To run the system, you will need to have Docker and Docker Compose installed on your machine. Once you have cloned the repository, navigate to the root of the project and run the following command:

```
docker-compose up -d --build
```

This will start the master and multiple slaves, and you will be able to send tasks to the master using the ```localhost:8080/solve``` endpoint.

Please note that this project is a coursework and is intended for educational purposes only.
