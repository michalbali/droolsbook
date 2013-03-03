require 'net/http'
require 'json'
http = Net::HTTP.new('localhost', 8080)
path = "/camelServer/kservice/rest/execute"
headers = {
  "Content-Type" => "text/plain"
}
post_data = {"batch-execution" => {                     
  "commands" => [ 
    { 
      "insert" => {
        "out-identifier" => "account",
        "return-object" => true,
        "object" => { 
          "droolsbook.decisiontables.bank.model.Account" => {
            "type" => "STUDENT",
            "balance" => "1000", 
            "currency" => "EUR" 
          }  
        }
      }
    },
    {
      "fire-all-rules" => ""
    }
  ]  
}}
resp, data = http.post(path, post_data.to_json, headers)
answer = JSON.parse(data)
puts answer["execution-results"]["results"]["result"]["value"]["droolsbook.decisiontables.bank.model.Account"]["interestRate"]
