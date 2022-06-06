FROM adoptopenjdk/openjdk11
#베이스로 할 이미지를 가져가야함
CMD ["./mvnw", "clean", "package"]
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
#무엇을 실행할건지 명시