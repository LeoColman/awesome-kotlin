FROM bellsoft/liberica-openjre-alpine:21.0.2
RUN apk --no-cache add curl
COPY /app-backend/build/install/awesome /app/backend/
COPY /app-frontend/dist /app/frontend/
ENTRYPOINT /app/backend/bin/awesome
