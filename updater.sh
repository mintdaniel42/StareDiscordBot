wget https://raw.githubusercontent.com/mintdaniel42/StareDiscordBot/master/docker-compose.yml

if command -v docker-compose &> /dev/null; then
  docker-compose up -d
else
  docker compose up -d
fi
