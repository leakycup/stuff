# inspired by http://stackoverflow.com/a/17069876/740482
import sys 
import codecs
import unicodedata

UTF8Writer = codecs.getwriter('utf8')
sys.stdout = UTF8Writer(sys.stdout)

ipfile = sys.argv[1]
opfile = sys.argv[2]

def remove_accents(input_str):
    nkfd_form = unicodedata.normalize('NFKD', unicode(input_str))
    accent_dropped = u"".join([c for c in nkfd_form if not unicodedata.combining(c)])
    nfkc_form = unicodedata.normalize('NFKC', accent_dropped)
    return nfkc_form

with codecs.open(ipfile, 'r', 'utf-8') as f, codecs.open(opfile, 'w', 'utf-8') as o:
    for line in f:
      o.write(remove_accents(line))

