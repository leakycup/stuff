#!/usr/bin/ruby

require 'json'

    def diffHash(json1, json2, file1, file2, parentKey)
        json1.each_key { |key|
            if (!json2.has_key?(key))
                return file2 + ' does not have key: ' + parentKey + ':' + key
            end
        }

        json2.each_key { |key|
            if (!json1.has_key?(key))
                return file1 + ' does not have key: ' + parentKey + ':' + key
            end
        }

        json1.each { |key, value1|
            value2 = json2[key]
            pathKey = parentKey + ':' + key
            error = diffJson(value1, value2, file1, file2, pathKey)
            if (!error.empty?)
                return error
            end
        }

        return ''
    end

    def diffArray(json1, json2, file1, file2, parentKey)
        length1 = json1.length()
        length2 = json2.length()
        if (length1 != length2)
            return 'for key: ' + parentKey + ', length of array differs. ' + length1.to_s + ' (in ' + file1 + ') vs ' + length2.to_s + ' (in ' + file2 + ')'
        end

        json1.each_index { |index|
            value1 = json1[index]
            value2 = json2[index]
            pathKey = parentKey + '[' + index.to_s + ']'
            error = diffJson(value1, value2, file1, file2, pathKey)
            if (!error.empty?)
                return error
            end
        }

        return ''
    end

    def diffJson(json1, json2, file1, file2, key)
        if (json1.class != json2.class)
            return 'for key: ' + key + ', type of value differ. ' + json1.class.to_s + ' vs ' + json2.class.to_s
        end
        error = ''
        if (json1.instance_of?(Hash))
            error = diffHash(json1, json2, file1, file2, key)
        elsif (json1.instance_of?(Array))
            error = diffArray(json1, json2, file1, file2, key)
        elsif (json1 != json2)
            error = 'for key: ' + key + ', values differ. ' + json1.to_s + ' vs ' + json2.to_s
        end

        return error
    end

if __FILE__ == $0
    file1 = ARGV[0]
    file2 = ARGV[1]

    content1 = IO.read(file1, :encoding => 'UTF-8')
    json1 = JSON.parse(content1)
    content2 = IO.read(file2, :encoding => 'UTF-8')
    json2 = JSON.parse(content2)

    error = diffJson(json1, json2, file1, file2, "<ROOT>")
    if (!error.empty?)
        warn error
    end
end
