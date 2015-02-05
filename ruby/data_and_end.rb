#!/usr/bin/ruby

require 'json'
require_relative 'jsondiff'


if __FILE__ == $0
    file1 = ARGV[0]

    content1 = IO.read(file1, :encoding => 'UTF-8')
    json1 = JSON.parse(content1)

    puts DATA.class
    puts DATA.to_path
    json2 = JSON.load(DATA)

    error = diffJson(json1, json2, file1, DATA, "<ROOT>")
    if (!error.empty?)
        warn error
    end
end

__END__

{
    "id": "1",
    "name": "testdata"
}
