require 'net/http'
require 'json'
http = Net::HTTP.new('localhost', 8080)
path = "/drools-server/knowledgebase/interestcalculation"
headers = {
  "Content-Type" => "application/json"
}
post_data = {"knowledgebase-request" => {                     
  :inOutFacts => {
    "named-fact" => [ { :id => "account", :fact => {
      "@class"=>"droolsbook.decisiontables.bank.model.Account",
      "type" => "STUDENT", "balance" => "1000", 
      "currency" => "EUR" } } ] 
  }
}}
resp, data = http.post(path, post_data.to_json, headers)
answer = JSON.parse(data)
puts answer["knowledgebase-response"]["inOutFacts"]\
["named-fact"][0]["fact"]["interestRate"]
