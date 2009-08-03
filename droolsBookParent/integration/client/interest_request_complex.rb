    require 'json'
    http = Net::HTTP.new('localhost', 8080)
    path = "/drools-server/knowledgebase/interestcalculation"
    post_data = {"knowledgebase-request" => {                     
                  :inOutFacts => {"named-fact" => [
                  	{:id => "y", :fact => {"@class" => "droolsbook.decisiontables.bank.model.Account", "type" => "SAVINGS", "balance" => "200", "startDate" => {"iMillis" => 1195689600000, "iChronology" => { "@class" => "org.joda.time.chrono.ISOChronology", "@resolves-to" => "org.joda.time.chrono.ISOChronology$Stub", "@serialization" => "custom"}}, "endDate" => {"iMillis" => 1199145600000, "iChronology" => { "@class" => "org.joda.time.chrono.ISOChronology", "@reference" => "../../startDate/iChronology"}}, "currency" => "EUR"}}]}
                                            }                    
                 }
    headers = {
      "Content-Type" => "application/json"
    }
    resp, data = http.post(path, post_data.to_json, headers)
        
    answer = JSON.parse(data)
    puts answer

    #{:id => "x", :fact => {"@class" => "droolsbook.decisiontables.bank.model.Account", "type" => "STUDENT", "balance" => "200", "currency" => "EUR"}},