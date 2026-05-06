# Étape 1 : Build de l'application Angular
FROM node:20 AS build
WORKDIR /app

# Installation des dépendances
COPY package*.json ./
RUN npm install

# Copie du code et build
COPY . .
RUN npm run build --configuration=production

# Étape 2 : Serveur de production (Nginx)
FROM nginx:stable-alpine
# Copie des fichiers compilés depuis l'étape de build
COPY --from=build /app/dist/nemia-view/browser /usr/share/nginx/html

# Copie d'une configuration Nginx personnalisée pour gérer le routage Angular
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]