; ; ; [:entity
; ; ;  ([:name "J"]
; ; ;   [:locations
; ; ;    ([:address [:slot "slot0"] [:port "1"]]
; ; ;     [:address [:slot "slot1"] [:port "2"]]
; ; ;     [:address [:slot "slot2"] [:port "3"]]
; ; ;     [:address [:slot "slot3"] [:port "4"]]
; ; ;     [:address [:slot "slot4"] [:port "5"]]
; ; ;     [:address [:slot "slot5"] [:port "6"]])])]

; ; ; (def t
; ; ;   {:entity 
; ; ;    [{:name "J"}
; ; ;     {:locations 
; ; ;      [{:address {:slot "slot0" :port "1"}} 
; ; ;       {:address {:slot "slot1" :port "2"}}
; ; ;       {:address {:slot "slot2" :port "3"}}
; ; ;       {:address {:slot "slot3" :port "4"}}
; ; ;       {:address {:slot "slot4" :port "5"}}
; ; ;       {:address {:slot "slot5" :port "6"}}]}]})



; ; ; (def TREE-VALUES
; ; ; 	(recursive-path [] p
; ; ; 	  (if-path [FIRST #(keyword? %)]
; ; ; 		[ALL p]
; ; ; 		STAY)))

; (def model 
;  [:<device> [[:hostname "J"] [:<interfaces> [[:interface [:<name> "eth0"] [:ip_address "10.0.18.1/24"]] [:interface [:<name> "eth1"] [:ip_address "10.0.19.1/24"]] [:interface [:<name> "eth2"] [:ip_address "10.0.15.2/24"]] [:interface [:<name> "eth3"] [:ip_address "10.0.13.2/24"]] [:interface [:<name> "eth4"] [:ip_address "10.0.11.2/24"]] [:interface [:<name> "eth5"] [:ip_address "10.0.9.2/24"]]]] [:router_bgp [:<asn> 65525] [:+synchronization false] [:<bgplist> [[:bgp [:router-id "192.0.0.1"]] [:bgp [:+always-compare-med true]] [:bgp [:+deterministic-med true]] [:bgp [:bestpath [:+compare-routerid true]]] [:bgp [:bestpath [:+as-path_confed true]]] [:bgp [:confederation [:identifier 100]]] [:bgp [:confederation [:peers #{"65527" "65528" "65529" "65530"}]]]]] [:<neighbors> [[:neighbor [:10.0.18.2 [:remote-as 200]]] [:neighbor [:10.0.19.2 [:remote-as 300]]] [:neighbor [:10.0.15.1 [:remote-as 65527]]] [:neighbor [:10.0.15.1 [:+next-hop-self true]]] [:neighbor [:10.0.15.1 [:send-community "both"]]] [:neighbor [:10.0.15.1 [:advertisement-interval "5"]]] [:neighbor [:10.0.13.1 [:remote-as 65528]]] [:neighbor [:10.0.13.1 [:+next-hop-self true]]] [:neighbor [:10.0.13.1 [:send-community "both"]]] [:neighbor [:10.0.13.1 [:advertisement-interval "5"]]] [:neighbor [:10.0.11.1 [:remote-as 65529]]] [:neighbor [:10.0.11.1 [:+next-hop-self true]]] [:neighbor [:10.0.11.1 [:send-community "both"]]] [:neighbor [:10.0.11.1 [:advertisement-interval "5"]]] [:neighbor [:10.0.9.1 [:remote-as 65530]]] [:neighbor [:10.0.9.1 [:+next-hop-self true]]] [:neighbor [:10.0.9.1 [:send-community "both"]]] [:neighbor [:10.0.9.1 [:advertisement-interval "5"]]] [:neighbor [:10.0.18.2 [:route-map [:rm-in "in"]]]] [:neighbor [:10.0.18.2 [:route-map [:rm-export-2 "out"]]]] [:neighbor [:10.0.19.2 [:route-map [:rm-in "in"]]]] [:neighbor [:10.0.19.2 [:route-map [:rm-export-2 "out"]]]] [:neighbor [:10.0.15.1 [:route-map [:rm-in "in"]]]] [:neighbor [:10.0.15.1 [:route-map [:rm-export-1 "out"]]]] [:neighbor [:10.0.13.1 [:route-map [:rm-in "in"]]]] [:neighbor [:10.0.13.1 [:route-map [:rm-export-1 "out"]]]] [:neighbor [:10.0.11.1 [:route-map [:rm-in "in"]]]] [:neighbor [:10.0.11.1 [:route-map [:rm-export-1 "out"]]]] [:neighbor [:10.0.9.1 [:route-map [:rm-in "in"]]]] [:neighbor [:10.0.9.1 [:route-map [:rm-export-1 "out"]]]]]]]]])


; ; (def z 
; ;   [:entity
; ;    (list   [:name "J"]
; ;       [:locations
; ;        (list 
; ;           [:address [:slot "slot0"] [:port "1"]]
; ;           [:address [:slot "slot1"] [:port "2"]]
; ;           [:address [:slot "slot2"] [:port "3"]]
; ;           [:address [:slot "slot3"] [:port "4"]]
; ;           [:address [:slot "slot4"] [:port "5"]]
; ;           [:address [:slot "slot5"] [:port "6"]])])])

; c {} (first coll) (reduce conj {} (rest coll)) 
;      ; coll))


; ; ; (def NODES
; ; ;   (recursive-path [] p
; ; ;     (if-path coll?
; ; ;       (continue-then-stay ALL p))))
      

; ; ; (multi-transform
; ; ;   [NODES
; ; ;    (if-path vector?
; ; ;      (terminal vec->map)
; ; ;      (terminal vec))]
     
; ; ;   data)
  

; (defn found-set [coll]
;  coll)

; (defn found-vec [coll]
;  (if (> (count coll) 2) 
;   (let [k (first coll)
;         me (reduce conj {} (rest coll))]    
;     (assoc {} k me))
;   coll))
;    ; (conj {} (rest #spy/p coll)))  
;      ; (asso


; (defn vpred? [coll]
;   ; (keyword? (first coll))) 
;   (seq? coll))
; ; (defn vec->map [coll]
; ;  (if (mod (count #spy/p (next coll)) 2)
; ;      (assoc {} (first coll) (reduce conj {} (rest coll))) 
; ;      coll))
;   ; (if (= (count coll) 2)
;   ; 	(conj {} coll)))

; (def NODES
;   (recursive-path [] p
;     (if-path coll?
;       (continue-then-stay ALL p))))
      

; (defn mt []
;     (multi-transform
;       [NODES
;        (cond-path 
;         set? (terminal found-set)
;         vector? (terminal found-vec))]
;       model))
; ;       