##!! this is included in ch4 DSL

[condition][]There is a Customer with firstName {name}=$customer : Customer(firstName == {name})
[consequence][]Greet Customer=System.out.println("Hello " + $customer.getFirstName());
