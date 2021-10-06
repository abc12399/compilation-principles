FROM openjdk:14
COPY . /myapp/
WORKDIR /myapp/
RUN javac src/*.java