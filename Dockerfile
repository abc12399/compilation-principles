FROM openjdk:14
COPY . /myapp/
WORKDIR /myapp/
RUN javac -cp src/ src/*.java -d dst/