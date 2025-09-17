FROM httpd:2.4
RUN sed -i 's/Listen 80/Listen ${PORT}/g' /usr/local/apache2/conf/httpd.conf
COPY build/ /usr/local/apache2/htdocs/
EXPOSE 8080
