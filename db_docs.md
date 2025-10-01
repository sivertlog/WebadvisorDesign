# Documentation of Database Elements

## Roles:
| Role Name     | Role ID |
| ------------- | ------- |
| Guest         | 100     |
| Student       | 200     |
| Professor     | 500     |
| Advisor       | 600     |
| Administrator | 700     |

## Permissions (+Roles):
| Permission Name       | Permission ID | Associated Role |
| --------------------- | ------------- | --------------- |
| VIEW_CLASS            | 100           | Guest           |
| VIEW_ACCOUNT_SELF     | 200           | Student         |
| EDIT_ACCOUNT_SELF     | 201           | Student         |
| REGISTER_CLASS_SELF   | 212           | Student         |
| DROP_CLASS_SELF       | 213           | Student         |
| VIEW_PLAN             | 220           | Student         |
| EDIT_PLAN             | 221           | Student         |
| REQUEST_CHANGE        | 572           | Professor       |
| VIEW_ACCOUNT_OTHER    | 600           | Advisor         |
| EDIT_ACCOUNT_OTHER    | 601           | Advisor         |
| REMOVE_PLAN           | 623           | Advisor         |
| APPROVE_PLAN          | 626           | Advisor         |
| EDIT_ACCOUNT_ADVANCED | 701           | Administrator   |
| CREATE_ACCOUNT        | 704           | Administrator   |
| DELETE_ACCOUNT        | 705           | Administrator   |
| EDIT_CLASS            | 711           | Administrator   |
| REGISTER_CLASS_OTHER  | 712           | Administrator   |
| DROP_CLASS_OTHER      | 713           | Administrator   |
| APPROVE_CHANGE        | 776           | Administrator   |
