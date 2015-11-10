import BaseHTTPServer
import SimpleHTTPServer
import SocketServer

class ColorHandler(SimpleHTTPServer.SimpleHTTPRequestHandler):
  log = open("./logs", "a")

  def logParams(self, path, client):
    if '?' not in path:
      return

    params = {}
    path = path.strip().split('?')[1]
    path = path.strip('/')
    print path
    keyValues = path.split('&')
    message=str(client) + "\t" + "\t".join(keyValues) + "\n"
    self.log.write(message)
    self.log.flush()

  def do_GET(self):
    print "do_GET called"
    print self.path
    self.logParams(self.path, self.client_address)
    SimpleHTTPServer.SimpleHTTPRequestHandler.do_GET(self)


PORT = 8420

Handler = ColorHandler

httpd = BaseHTTPServer.HTTPServer(("", PORT), Handler)

print "serving at port", PORT
httpd.serve_forever()
