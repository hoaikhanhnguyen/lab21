// lab21.js
// author: Khanh Nguyen

// remove existing drug db if it exists
db.drug.drop();

print('Creating drug db...');
db.createCollection('drug');

let d = [
    {_id:1, name: 'acetaminophen'},
    {_id:2, name: 'lisinopril'},
    {_id:3, name: 'loratadine'},
    {_id:4, name: 'lovastatin'}
];

db.drug.insertMany(d);
print("Drugs added to drug database");


// remove existing pharmacy db if it exists
db.pharmacy.drop();

print('Creating pharmacy db...');
db.createCollection('pharmacy');

let p = [
    {  _id: 1, name: 'cvs', address: '123 main', phone: '813-774-1200',
        drugCosts: [{ drugName: 'lisinopril', cost: 7.5 },
                    { drugName: 'loratadine', cost: 5.5 },
                    { drugName: 'lovastatin', cost: 8.5 },
                    { drugName: 'acetaminophen', cost: 7.5 }
         ]
    },
    {  _id: 2, name: 'rite aid', address: '3325 S Bristol St', phone: '714-79-4060',
        drugCosts: [{ drugName: 'lisinopril', cost: 6.5 },
                    { drugName: 'loratadine', cost: 5.0 },
                    { drugName: 'lovastatin', cost: 8.0 },
                    { drugName: 'acetaminophen', cost: 5.5 }
        ]
    },
    {  _id: 3, name: 'walgreens', address: '13052 Newport Ave', phone: '714-505-6021',
        drugCosts: [{ drugName: 'lisinopril', cost: 7.0 },
                    { drugName: 'loratadine', cost: 6.0},
                    { drugName: 'lovastatin', cost: 7.0 },
                    { drugName: 'acetaminophen', cost: 6.0 }
        ]
    }   
];

db.pharmacy.insertMany(p);
print('Pharmacies added to pharmacy db');