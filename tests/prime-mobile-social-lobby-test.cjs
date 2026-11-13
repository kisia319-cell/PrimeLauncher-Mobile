const fs=require("node:fs");const path=require("node:path");const assert=require("node:assert/strict");
const read=p=>fs.readFileSync(path.join(process.cwd(),p),"utf8");
const base="app/src/main/assets/prime/",html=read(base+"index.html"),world=read(base+"prime-world.js"),online=read(base+"prime-online.js");
for(const id of ["friends-page","prime-friend-search","prime-friend-find","prime-friend-list","prime-lobby-invites","prime-lobby-create","prime-lobby-panel","prime-play-bots"])assert.ok(html.includes(id),"Missing Android Prime social UI: "+id);
for(const method of ["friends","searchPlayers","addFriend","respondFriend","lobby","createLobby","inviteLobbyFriend","respondLobbyInvite","startLobby","leaveLobby"])assert.ok(online.includes(method+":"),"Missing Android Prime client method: "+method);
assert.ok(world.includes("НАЧАТЬ ДУЭЛЬ 1 × 1")&&world.includes("Играть против ботов"),"Android must expose both bot and friend-duel flows");
console.log("Android Prime social/lobby integration: all assertions passed");