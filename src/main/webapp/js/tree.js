function documentOnClick(e){
  var ie_old = (e == undefined);
  var n = ie_old ? event.srcElement : e.target;
  if(n == null) return;
  if(n.tagName != 'LI') n = n.parentNode;
  if(n.id=="foldhead"){
    var folderContent = ie_old ? n.childNodes[1] : n.nextSibling.nextSibling;
    if(folderContent == null) return;
    if(folderContent.style.display=="none"){
      folderContent.style.display="";
      n.style.listStyleImage="url(img/tree_open.png)";
    }
    else{
      folderContent.style.display="none";
      n.style.listStyleImage="url(img/tree_fold.png)";
    }
  }
}

function treeExpandAll(){
  var allul = document.getElementsByTagName('UL');
  if(!allul || !allul.length) return;
  for(var i = 0; i < allul.length; i++){
    var eul = allul[i];
    if(eul.id!="foldlist")continue;
    eul.style.display="";
  }
}

function treeCollapseAll(){
  var allul = document.getElementsByTagName('UL');
  if(!allul || !allul.length) return;
  for(var i = 0; i < allul.length; i++){
    var eul = allul[i];
    if(eul.id!="foldlist")continue;
    eul.style.display="none";
  }
}

document.onclick = documentOnClick;
