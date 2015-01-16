#!/usr/bin/ruby

require 'json'

if __FILE__ == $0
    file = ARGV[0]

    content = IO.read(file, :encoding => 'UTF-8')
    json = JSON.parse(content)
    docs = json['response']['docs']
    numFound = json['response']['numFound']
    puts 'num docs: ' + docs.length().to_s
    puts 'numFound: ' + numFound.to_s
end
