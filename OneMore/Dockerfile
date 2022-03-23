# syntax=docker/dockerfile:1
FROM golang

WORKDIR /app

RUN apt-get -y update
RUN apt-get -y install git

COPY go.mod ./
COPY go.sum ./

COPY . ./

RUN go get github.com/ZeLebo/POC@upgrade

EXPOSE 60494

CMD ["go", "run", "cmd/main.go"]

#RUN go build -o /serv
#CMD ["/serv"]