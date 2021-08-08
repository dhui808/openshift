# s2i-do288-nginx
FROM registry.access.redhat.com/ubi8:8.0

RUN yum install -y --disableplugin=subscription-manager --nodocs nginx \
  && yum clean all

ADD nginxconf.sed /tmp/
COPY ./s2i/bin/ /usr/libexec/s2i

RUN touch /run/nginx.pid \
  && sed -i -f /tmp/nginxconf.sed /etc/nginx/nginx.conf \
  && chgrp -R 0 /var/log/nginx /run/nginx.pid \
  && chmod -R g=u /var/log/nginx /run/nginx.pid /usr/share/nginx/html \
  && echo 'Hello from the Nginx S2I builder image' > /usr/share/nginx/html/index.html

# This default user is created in the openshift/base-centos7 image
USER 1001

EXPOSE 8080

CMD ["/usr/libexec/s2i/run"]
