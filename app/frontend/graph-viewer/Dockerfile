#Stage 1
FROM node:14-alpine as builder
WORKDIR /app
COPY package*.json .
COPY package*.lock .
RUN npm install
COPY . .
RUN npm build

#Stage 2
FROM nginx:1.19.0
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=builder /app/build .
EXPOSE 80

ENTRYPOINT ["nginx", "-g", "daemon off;"]