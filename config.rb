set :js_dir, 'javascripts'
set :css_dir, 'stylesheets'
set :images_dir, 'images'

configure :build do
    activate :minify_css
    activate :minify_javascript
    activate :asset_hash
    activate :relative_assets
end
