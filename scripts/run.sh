#! /bin/bash

# 기존거는 죽여버려
# 중간에는 프로젝트 제목이니까 잘 바꿔주자
echo "> Kill Current Application"
CURRENT_PID=$(ps -ef | grep geha-sajang | grep java | awk '{print $2}')
sudo kill -15 ${CURRENT_PID}

# 경로 수정
echo "> Change path"
cd /home/ubuntu/geha-sajang

# 기존의 nohup.out 삭제
sudo rm /home/ubuntu/nohup.out
echo "> Remove nohup.out"

# 새로 어플리케이션 실행
nohup java -jar -Dspring.profiles.active=deploy /home/ubuntu/geha-sajang/build/libs/* > /home/ubuntu/nohup.out 2>&1 &
echo "> Now new WAS runs."

