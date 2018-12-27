import configparser
import os
import smtplib  
import logging
from email.mime import multipart
from email.mime.text import MIMEText
from smtplib import SMTPException

#创建日志
log_format = '%(filename)s %(asctime)s %(levelname)s: %(message)s'      
logging.basicConfig(format = log_format,level = logging.ERROR,filename = 'error.log',filemode = 'w')
logger = logging.getLogger(__name__)
 
#创建配置文件解析对象
config = configparser.ConfigParser()
#加载配置文件
config.read("MailConfiguration.ini")
#获取参数
sender = config.get('Mail', 'sender')
receiver =  config.get('Mail', 'receiver') 
files_path = config.get('File', 'path')
mail_host = config.get('Mail', 'mail_host')
mail_port = config.get('Mail', 'mail_port')
mail_pass = config.get('Mail', 'mail_pass')

msg = multipart.MIMEMultipart()  
msg['from'] =  sender
msg['to'] =  receiver
#标题
msg['subject'] = config.get('Title','title')  
#正文
content = MIMEText(config.get('Body', 'body'))
msg.attach(content)
smtp = None

try:
    #添加多个附件
    for file_path in files_path.split(','):
        basename = os.path.basename(file_path)
        f = open(file_path,'rb')
        
        att = MIMEText(f.read(),'base64','utf-8') 
        att["Content-Type"] = 'application/octet-stream'
        att.add_header('Content-Disposition', 'attachment',filename=('gbk', '', basename))
        msg.attach(att)
    
    smtp = smtplib.SMTP() #登录邮箱服务器
    smtp.connect(mail_host,mail_port) #连接邮箱服务器
    smtp.login(sender,mail_pass) #开始登录
    smtp.sendmail(sender,receiver,msg.as_string()) #发送邮件
except IOError as e:
    logger.error(e)
except SMTPException as e:
    logger.error(e)
except Exception as e:
    logger.error(e)
finally:
    if smtp:
        smtp.close()

