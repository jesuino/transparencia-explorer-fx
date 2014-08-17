import urllib2

siglas = open('todos-estados.txt', 'r')
for line in siglas:
	codigo = line.replace('\n', '').lower() + '.gif'
	img_url = 'http://www.sogeografia.com.br/figuras/bandeiras/estados/' + codigo
	print img_url
	req = urllib2.Request(img_url)
	response = urllib2.urlopen(req)
	img = response.read()
	f = open(codigo, 'w')
	f.write(img)
