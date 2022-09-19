FROM openjdk:11
#도커허브에 있는 베이스로 할 이미지를 가져가야함
#RUN도커 이미지가 실행될 때 컨테이너 안에서 실행할 명령입니다.
 #스크립트의 명령에 따라 컨테이너 안에서는 /echo 디렉터리가 만들어집니다.]
#cmd 컨테이너 안에서 실행할 프로세스를 지정합니다.
#./mvnw clean package 명령 실행
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar","/app.jar"]
#무엇을 실행할건지 명시