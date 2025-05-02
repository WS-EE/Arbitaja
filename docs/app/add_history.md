# How to add points to competitor in a competition.

## Info
You require 4 parameters and 1 http header

Parameters:
  - competition_id
  - criteria_id
  - competitor_id
  - points
> All parameters can be seen on the `competition edit` page under the `admin panel`

Header:
  - X-API-KEY
> This is the `API_KEY` parameter in the `.env` file.

## Examples
These are just examples. You might use `https` instead of `http`.

The url which is used (`arbitaja.ee`) might also differe based on what the environmental variable `VITE_APP_BASE_URL` was set.

This variable is used in generation of the frontend and also in [CORS](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing) on the backend.

### cURL
This is an example with test data on how to do it with curl.
```bash
curl 'http://arbitaja.ee/api/v1/competition/criteria/history/add?competition_id=35&criteria_id=30&competitor_id=47&points=2' \
    -X 'POST' \
    -H 'X-API-KEY: 'arbitaja_generic_api_key'
```
Or if you want to sepratelly bring out the http params add the `-G` option
```bash 
curl -X 'POST' -G 'http://arbitaja.ee/api/v1/competition/criteria/history/add' \
    -d competition_id=35 \
    -d criteria_id=30 \
    -d competitor_id=47 \
    -d points=2 \
    -H 'X-API-KEY: arbitaja_generic_api_key'
```

### Ansible
You can also use ansible tasks to add history.

Like so:
> This is from the ansible task section of the playbook
```yaml
- name: Add competition criteria history
  ansible.builtin.uri:
    url: "http://arbitaja.ee/api/v1/competition/criteria/history/add"
    method: POST
    headers:
      X-API-KEY: "arbitaja_generic_api_key"
    body:
      competition_id: 35
      criteria_id: 30
      competitor_id: 47
      points: 2
    body_format: form-urlencoded
    return_content: yes
  delegate_to: localhost
```
