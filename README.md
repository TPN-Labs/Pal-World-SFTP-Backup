# Pal World Backuper

This repo represents my history of trying to backup my pal's Pal World save files. 
I'm not sure if I'll ever get it to work, but I'm trying. 

It intends to be a AWS Lambda function that will be triggered by an EventBridge (former known as CloudWatch Events) rule. 
This EventBridge basically will act as a cron job, triggering the Lambda function every 5 minutes.

At this time, I've tried using Java 21 (using JSCh) and Python 3.12 (using Paramiko) to connect to the SFTP server and 
download the save files. Both attempts failed.


