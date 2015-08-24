# GalleryPicker
GaleeryPicker allows to pick up photo previously sorted by folders. 


<b>Screenshots, version from 23.08.2015:</b> 
<br>


 <table style="width:100%">
  <tr>
    <td>![alt tag](https://lh3.googleusercontent.com/3qez1DVBv-gzLPk9cBnkTkMr60F8STWSyuPkRbs_iUDd99jg9U6MByLxanZm4JfojsBcBSPzavfmQLI=w1890-h840)</td>
    <td>![alt tag](https://lh5.googleusercontent.com/2HhJJIY0Y3zDkLKLHWNQTid49txdSuam4jGHrJpHk8SgjTUoa1yGYpOlSX83v_Nf50mSU_mXXeX_HoY=w1890-h840)</td>
    <td>![alt tag](https://lh3.googleusercontent.com/rlxpWEsOpteU5wAeKBJVWd1vj6ehgblSR2xSVKnAxhJzxOm52wRuS3XrrIgZ_rgwlDVP3ABAiMuE-IY=w1890-h840)</td>
  </tr>
</table> 

<b>Screenshots, version from 24.08.2015:</b> 
<br>
Added:
- filtering
- rough span count changing
Found issues:
- outOfMemooryException -> preformance issues
- grid span changing changes whole RecyclerView -> filtering and scroll position is being lost each time
<br>

 <table style="width:100%">
  <tr>
    <td>![alt tag](https://lh4.googleusercontent.com/FtdLAXzEgXI4up6nNils9c0R7tbjH2myGDix5c2RLXEm7Nhvq4JNZJvi38pcXtV8KGAJVFCRkJ3gzco=w1890-h840)</td>
    <td>![alt tag](https://lh6.googleusercontent.com/5OPR9uSknbBXiLcMV1X4fnXU1C1xPuklUofSdKOsVjYHDbf-J9ScI0DRnBrdZEBUjZa-NKwHg75c5vk=w1890-h840)</td>
    <td>![alt tag](https://lh3.googleusercontent.com/n0qrtDwsQ3nDInwWwvi9R9ip0Xocd-68skIxH10u8Dmoqmd0ML88P64O6XPyCjDjdHxAGRe1qbha3w8=w1890-h840)</td>
  </tr>
</table> 



Made of:
- recycler view
- universal image loader


Features:
- custom quick return pattern(you can read about it here: http://www.androiduipatterns.com/2012/08/an-emerging-ui-pattern-quick-return.html)


In development:
- sorting type could be chosen from time ordering or alphabetic ascending
- recyclerView data filtering based on user input
- change gridView tiles quantity depending on chosen mode(2 or 4 rows per screen width)


Further development:
- image previewert

<b> How does it work? </b>

1. Images: how it is found byb app? - It is very simple. Android has built-in deamon service that monitoring changes of your filesystem and allows you tooptimie access to different resources by indexing files in its database. So in this case the only thing you should do to achieve desired result is to query database for images(by the way both EXTERNAL and INTERNAL SD card)
2. Database quering results. It is structed by folders, how is it performing in realtime? As for me it is an interesting question. I solved sorting issue with quite straightforward solution: as far as all image file paths are the same for specified folder, I need just to remove file name from full file path. So after doing this thing I got XX identical Strings which automacally meeans for me that I have to remove this redundant Strings to show only unique folder names only ones. This problem I solved with creating HashSet object. Using HashSet allowed me to reject all redundant Strings due to main property of the HashSet object: every inner object that is adding to HashSet has to be unique in other way it rejects.
3. So as far it could seen you querying algorythm is unreliable due to O(1) complexity, why do you think that this code should be published to community? Actually, I don't give a **** what community think about my solutions. The only thing I could say to deffence myself in this situation: I tested code on API16 with ~500 images. Querying results were received in ~500 ms and further filtering with HashSet ~50ms, so in realtime you wouldn't notice quering or rendering problems, but I definately should test situation with 1000 or pictures.
