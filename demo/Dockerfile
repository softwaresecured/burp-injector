FROM python:3
ADD app.py .
ADD static/main.html static/main.html
ADD static/ static/
RUN apt-get -y update
RUN apt-get -y install dnsutils
RUN pip install Flask
EXPOSE 5000
CMD ["python", "./app.py"]
