# syntax=docker/dockerfile:1
FROM golang

WORKDIR /backend

RUN apt-get -y update
RUN apt-get -y install git

COPY go.mod ./
COPY go.sum ./

COPY . ./

EXPOSE 60494

# RUN go get github.com/ZeLebo/POC@zhora

RUN go build cmd/main.go
ENTRYPOINT ["./main"]