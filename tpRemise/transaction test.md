curl -i -X POST http://localhost:8080/transactions \                                                                                                 -H 

"Content-Type: application/json" 

\    -d 

'{"montant": 2000}'



●

\# existing transaction

  curl -i http://localhost:8080/transactions/1                                                                                                                                                                                                                                                          

\# non-existent

​                                                                                                                                     curl -i http://localhost:8080/transactions/999                                                                                                                       

\# invalid id

​                                                                                                                                       curl -i http://localhost:8080/transactions/abc                                                

