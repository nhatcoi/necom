FROM nginx:1.21.6-alpine

COPY nginx.conf /etc/nginx/nginx.conf

WORKDIR /usr/share/nginx/html

ARG BUILD_DIR=build
ADD ${BUILD_DIR} /usr/share/nginx/html
