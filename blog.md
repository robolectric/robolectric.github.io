---
---

# Posts:

<ul>
  {% for post in site.posts %}
    <li>
      <h3><a href="{{ post.url }}">{{ post.title }}</a></h3>
      <div>{{ post.excerpt }}</div>
       — {{ post.date }}
    </li>
  {% endfor %}
</ul>