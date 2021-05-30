const express = require('express');
const cors = require('cors'); 
const path = require('path')
const axios = require('axios').default;
const { parse } = require('path');
const app = express();
const port = process.env.PORT || 8080;


base_tmdb = 'https://api.themoviedb.org/3/'
api_key = '1f047a8a3c39611ebf69480af6d434ae'
api_key_dict = {"api_key":api_key}
backdrop_base = 'https://image.tmdb.org/t/p/original'
yt_base = "https://www.youtube.com/embed/"
avatar_base = "https://image.tmdb.org/t/p/original"

app.use(cors())



function extract_fields(data,fields) {
    var months = ["January ", "February ", "March ", "April ", "May ", "June ", "July ", "August ", "September ", "October ", "November ", "December "];
    if(data.constructor.name !=='Array') {
        data = [data]
    }

    parsed_items = []
    data.forEach(function (raw_item,index) {

        item_parsed = {}
        fields.forEach(function (field,index) {

            if(field in raw_item) {

                if(field=='name') {
                    item_parsed['title'] = raw_item[field]
                } else if(['backdrop_path'].includes(field)) {
                    if(raw_item[field]) {
                        item_parsed[field] = backdrop_base + raw_item[field]
                    } else {
                        item_parsed[field] = "https://bytes.usc.edu/cs571/s21_JSwasm00/hw/HW6/imgs/movie-placeholder.jpg"
                    }
                }else if(field=='poster_path') {
                    if(raw_item[field]) {
                        item_parsed[field] = backdrop_base + raw_item[field]
                    } else {
                        item_parsed[field] = "https://cinemaone.net/images/movie_placeholder.png"
                    }
                } else if(field=='profile_path') {
                    if(raw_item['profile_path']) {
                        item_parsed[field] = "https://image.tmdb.org/t/p/w500/" + raw_item[field]
                    } else {
                        item_parsed[field] = "https://bytes.usc.edu/cs571/s21_JSwasm00/hw/HW6/imgs/person-placeholder.png"
                    }

                } 
                else if(field=='key') {

                    if(raw_item['site']!='YouTube') {
                        item_parsed[field] = 'tzkWB85ULJY'
                    } else {
                        item_parsed[field] = raw_item[field]
                    }
                } else if(field=='genres') {
                    item_parsed[field] = raw_item['genres'].map(genre => genre['name'])
                } else if(field=='spoken_languages') {
                    item_parsed[field] = raw_item['spoken_languages'].map(lang => lang['english_name'])
                } else if(field=='author_details'){
                    auth_details = raw_item[field]
                    if('avatar_path' in auth_details && auth_details['avatar_path']) {
                        avatar = auth_details['avatar_path']
                        if(avatar.startsWith('/https')) {
                            item_parsed['avatar_path'] = avatar.substring(1)
                        } else {
                            item_parsed['avatar_path'] = avatar_base+avatar
                        }
                    } else {
                        item_parsed['avatar_path'] = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRHnPmUvFLjjmoYWAbLTEmLLIRCPpV_OgxCVA&usqp=CAU"
                    }

                    if('rating' in auth_details) {
                        rating = auth_details['rating']
                        if(!rating) {
                            rating = 0
                        }
                        item_parsed['rating'] = rating
                    }
                } else if(field=='gender') {
                    gender = raw_item[field]
                    if(gender==1) {
                        item_parsed[field] = 'Female'
                    } else if(gender==2) {
                        item_parsed[field] = 'Male'
                    } else {
                        item_parsed[field] = 'undefined'
                    }    
                } else if(field=='imdb_id' && raw_item[field]) {
                        item_parsed[field] = 'https://imdb.com/name/'+raw_item[field]
                } else if(field=='facebook_id' && raw_item[field]) {

                        item_parsed[field] = 'https://facebook.com/'+raw_item[field]
               
                } else if(field=='instagram_id' && raw_item[field]) {
                    
                        item_parsed[field] = 'https://instagram.com/'+raw_item[field]
                    
                } else if(field=='twitter_id' && raw_item[field]) {
                    
                        item_parsed[field] = 'https://twitter.com/'+raw_item[field]
                    
                } else if(['runtime','episode_run_time'].includes(field) ) {
                    if(Array.isArray(raw_item[field])) {
                        time = raw_item[field][0]
                    } else {
                        time = raw_item[field]
                    }
                    if(time>60) {
                        hrs = Math.floor(time/60)
                        item_parsed[field] = hrs + ' hrs ' + (time-hrs*60) + ' mins' 
                    } else {
                        item_parsed[field] = time + ' mins' 
                    }
                    
                } else if(['first_air_date','release_date'].includes(field) ) {
                    item_parsed[field] = raw_item[field].slice(0,4)
                    
                } else if (field=='created_at') {
                    if(raw_item["created_at"]) {
                        date_str = new Date(raw_item[field]).toDateString()
                        item_parsed[field] = date_str.substring(0,3) + "," + date_str.substring(3)
                    } else {
                        item_parsed[field] = ""
                    }
                    

                }
                else {
                    item_parsed[field] = raw_item[field]
                }

            }
        })

        parsed_items.push(item_parsed)
        
         
    }
    )

    return parsed_items
}


app.get('/', (req, res) => res.send('Hello World!'));

//Search
app.get('/search', (req, res) => {
    
    query = req.query.query;
    req_url = base_tmdb+'search/multi'
    params = {params:{"api_key":api_key, "query":query, "language":"en-US", "page":1}}
    axios.get(req_url,params)
    .then(function (response) {
    if (!('results' in response.data)) {
        res.json([])
    } else {

        raw_json = response.data.results
        items = []
        raw_json.forEach(function (item,index) {
            if(item.media_type!='person') {
                items.push(item)
            }
        })
        fields = ['id','title','name','backdrop_path','media_type','vote_average','release_date','first_air_date']
        parsed_data = extract_fields(items,fields)
        parsed_data = parsed_data.filter(item => item.backdrop_path != "https://bytes.usc.edu/cs571/s21_JSwasm00/hw/HW6/imgs/movie-placeholder.jpg")
        res.send(parsed_data.slice(0,20))


    }
    
    })
    .catch(function (error) {
        res.json([])
    })
    .then(function (){});

});

//Trending
app.get('/trending', (req, res) => {
    media_type=req.query.media_type
    req_url = base_tmdb+'trending/'+media_type+'/day'
    params = {params:api_key_dict}
    axios.get(req_url,params)
    .then(function (response) {
        if (!('results' in response.data)) {
            res.json([])
        } else {
            raw_json = response.data.results
            fields = ['id','title','name','poster_path']
            parsed_data = extract_fields(raw_json,fields)
            res.send(parsed_data.slice(0,6))
        }
    })
    .catch(function (error) {
        res.json([])
    })
    .then(function (){});

});

//Top-rated
app.get('/top-rated', (req, res) => {

    media_type=req.query.media_type
    req_url = base_tmdb+media_type+'/top_rated'
    params = {params:{"api_key":api_key, "language":"en-US", "page":1}}
    axios.get(req_url,params)
    .then(function (response) {
        if (!('results' in response.data)) {
            res.json([])
        } else {
            raw_json = response.data.results
            fields = ['id','title','name','poster_path']
            parsed_data = extract_fields(raw_json,fields)
            res.send(parsed_data.slice(0,10))
        }
    })
    .catch(function (error) {
        res.json([])
    })
    .then(function (){});

});

//Currently playing movies
app.get('/curr-playing-mov', (req, res) => {

    req_url = base_tmdb+'movie/now_playing'
    params = {params:{"api_key":api_key, "language":"en-US", "page":1}}
    axios.get(req_url,params)
    .then(function (response) {
        if (!('results' in response.data)) {
            res.json([])
        } else {
            raw_json = response.data.results
            fields = ['id','title','poster_path']
            parsed_data = extract_fields(raw_json,fields)
            res.send(parsed_data.slice(0,6))
        }
    })
    .catch(function (error) {
        console.log(error)
        res.json([])
    })
    .then(function (){});

});

//Popular
app.get('/popular', (req, res) => {

    media_type=req.query.media_type
    req_url = base_tmdb+media_type+'/popular'
    params = {params:{"api_key":api_key, "language":"en-US", "page":1}}
    axios.get(req_url,params)
    .then(function (response) {
        if (!('results' in response.data)) {
            res.json([])
        } else {
            raw_json = response.data.results
            fields = ['id','title','name','poster_path']
            parsed_data = extract_fields(raw_json,fields)
            res.send(parsed_data.slice(0,10))
        }
    })
    .catch(function (error) {
        res.json([])
    })
    .then(function (){});

});

//Recommended
app.get('/recommendations', (req, res) => {

    media_type=req.query.media_type
    id = req.query.id
    req_url = base_tmdb+media_type+'/'+id+'/recommendations'
    params = {params:{"api_key":api_key, "language":"en-US", "page":1}}
    axios.get(req_url,params)
    .then(function (response) {
        if (!('results' in response.data)) {
            res.json([])
        } else {
            raw_json = response.data.results
            fields = ['id','title','name','poster_path']
            parsed_data = extract_fields(raw_json,fields)
            res.send(parsed_data.slice(0,10))
        }
    })
    .catch(function (error) {
        res.json([])
    })
    .then(function (){});

});



//Video
app.get('/videos', (req, res) => {

    media_type=req.query.media_type
    id = req.query.id
    req_url = base_tmdb+media_type+'/'+id+'/videos'
    params = {params:{"api_key":api_key, "language":"en-US", "page":1}}
    axios.get(req_url,params)
    .then(function (response) {
        if (!('results' in response.data)) {
            res.json([])
        } else {
            raw_json = response.data.results
            fields = ['site','key','type']
            parsed_data = extract_fields(raw_json,fields)
            trailers = parsed_data.filter(item => item.type === 'Trailer' && item.key!=='tzkWB85ULJY')
            if(trailers.length==0) {
                res.send({})
            } else {
                res.send(trailers[0])
            }

        }
    })
    .catch(function (error) {
        res.json([])
    })
    .then(function (){});

});

//Details
app.get('/details', (req, res) => {
    
    media_type=req.query.media_type
    id = req.query.id
    
    //get item details
    req_url = base_tmdb+media_type+'/'+id
    params = {params:{"api_key":api_key, "language":"en-US", "page":1}}
    axios.get(req_url,params)
    .then(function (response) {
            raw_json = response.data
            fields = ['title','name','genres','spoken_languages','release_date','first_air_date','runtime','episode_run_time','overview','vote_average','tagline','backdrop_path','poster_path']
            parsed_data = extract_fields(raw_json,fields)
            res.send(parsed_data)       
        
    })
    .catch(function (error) {
        console.log(error)
        res.json([])

    })
    .then(function (){});
});

//Reviews
app.get('/reviews', (req, res) => {
    //get item reviews
    media_type=req.query.media_type
    id = req.query.id
    req_url = base_tmdb+media_type+'/'+id+'/reviews'
    params = {params:{"api_key":api_key, "language":"en-US", "page":1}}
    axios.get(req_url,params)
    .then(function (response) {
        if (!('results' in response.data)) {
            res.json([])
        } else {
            raw_json = response.data.results
            fields = ['author','content','created_at','url','rating','author_details']
            parsed_data = extract_fields(raw_json,fields)
            res.send(parsed_data.slice(0,3))
        }
            
        
    })
    .catch(function (error) {
        console.log(error)
        res.json([])
    })
    .then(function (){});

});


//Cast
app.get('/cast', (req, res) => {

    media_type=req.query.media_type
    id = req.query.id
    req_url = base_tmdb+media_type+'/'+id+'/credits'
    params = {params:{"api_key":api_key, "language":"en-US", "page":1}}
    axios.get(req_url,params)
    .then(function (response) {
        if (!('cast' in response.data)) {
            res.json([])
        } else {
            raw_json = response.data.cast
            items = []
            fields = ['id','name','profile_path']
            parsed_data = extract_fields(raw_json,fields)
            res.send(parsed_data.slice(0,6))
        }
    })
    .catch(function (error) {
        res.json([])
    })
    .then(function (){});

});





app.listen(port, () => console.log(`Backend app listening on port ${port}!`));

module.exports = app;
