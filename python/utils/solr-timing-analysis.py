import sys
import codecs
from datetime import timedelta
from datetime import datetime
from collections import defaultdict

UTF8Writer = codecs.getwriter('utf8')
sys.stdout = UTF8Writer(sys.stdout)
sys.stderr = UTF8Writer(sys.stderr)

FORMAT = '%Y-%m-%d %H:%M:%S.%f'
BUCKET_LEN = timedelta(minutes=1)
RESPONSE_TIME_BUCKET = 10

log_file = sys.argv[1]
average_response_time_file = sys.argv[2]
response_time_distribution_file = sys.argv[3]

log_events = []

with codecs.open(log_file, 'r', 'utf-8') as f:
  for line in f:
    parts = line.strip().split("\t")
    if (len(parts) < 3):
      sys.stderr.write(line)
      continue
    t = parts[0] + '000' #convert the last component from ms to microsecond
    url = parts[1]
    qtime = int(parts[2])

    log_time = datetime.strptime(t, FORMAT)
    log_events.append((log_time, url, qtime))

bucket_to_num_requests = defaultdict(int)
bucket_to_response_time = defaultdict(int)
response_time_to_num_requests = defaultdict(int)
total_requests = 0

current_bucket = datetime.strptime('1900-01-01 00:00:00.000', FORMAT)

for (log_time, url, qtime) in sorted(log_events, key=lambda x: x[0]):
    while (log_time >= (current_bucket+BUCKET_LEN)):
      current_bucket += BUCKET_LEN
    bucket_to_response_time[current_bucket] += qtime
    bucket_to_num_requests[current_bucket] += 1

    response_time_bucket = (qtime/RESPONSE_TIME_BUCKET) * RESPONSE_TIME_BUCKET
    response_time_to_num_requests[response_time_bucket] += 1

    total_requests += 1

with codecs.open(average_response_time_file, 'w', 'utf-8') as f:
  f.write("time bucket\tnum_requests\taverage_response_time\taverage_rps\n")
  for bucket in sorted(bucket_to_num_requests):
    num_requests = bucket_to_num_requests[bucket]
    response_time = bucket_to_response_time[bucket]
    average_response_time = float(response_time)/float(num_requests)
    average_rps = float(num_requests)/float(BUCKET_LEN.total_seconds())
    f.write(str(bucket) + "\t" + str(num_requests) + "\t" + str(average_response_time) + "\t" + str(average_rps) + "\n")

with codecs.open(response_time_distribution_file, 'w', 'utf-8') as f:
  f.write("response_time_bucket\tnum_requests\tpercentage\tcumulative_percentage\n")
  cumulative_num_requests = 0
  for response_time in sorted(response_time_to_num_requests):
    num_requests = response_time_to_num_requests[response_time]
    cumulative_num_requests += num_requests
    percentage = float(num_requests)*100/float(total_requests)
    cumulative_percentage = float(cumulative_num_requests)*100/float(total_requests)
    f.write(str(response_time) + "\t" + str(num_requests) + "\t" + str(percentage) + "\t" + str(cumulative_percentage) + "\n")
