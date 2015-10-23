from flask import Flask, jsonify, request, make_response
from flask_pushjack import FlaskGCM
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy.sql import func
from flask import render_template, redirect, url_for
from random import randint
from time import sleep
from loginmod import makerequest
import time

config = {
	'GCM_API_KEY' : 'AIzaSyDAM_bb9AEDZKwBtj8BJGosI7EIguRMt1E'
}

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///gcm_sender_2.db'
app.config['SECRET_KEY'] = 'HALO'
app.config.update(config)

db = SQLAlchemy(app)

client = FlaskGCM()
client.init_app(app)

class User(db.Model):
    id = db.Column(db.Integer, primary_key = True, autoincrement = True)
    u_studentid = db.Column(db.String)
    u_blue = db.Column(db.String)
    u_token = db.Column(db.String)
    u_register_date = db.Column(db.String)
    u_permission_number = db.Column(db.String) # 1 -> All (Profs, SBG), 2 -> Partial(Created Groups), 3 -> No one(Banned people)
    u_designation = db.Column(db.String)
    u_active = db.Column(db.Integer)

    def __init__(self, u_studentid, u_blue, u_token,u_register_date, u_permission_number, u_designation):
    	self.u_studentid = u_studentid
    	self.u_blue = u_blue
    	self.u_token = u_token
    	self.u_register_date = u_register_date
    	self.u_permission_number = u_permission_number
    	self.u_designation = u_designation
    	self.u_active = 1

class Broadcast(db.Model):
	id = db.Column(db.Integer, primary_key = True, autoincrement = True)
	b_title = db.Column(db.String)
	b_content = db.Column(db.Text)
	b_sender = db.Column(db.String)
	b_for_group = db.Column(db.String)
	b_date_post = db.Column(db.String)
	b_date_event = db.Column(db.String)
	b_location = db.Column(db.String)
	b_attendance = db.Column(db.Integer)
	b_active = db.Column(db.Integer)

	def __init__(self,b_title,b_content,b_sender,b_for_group,b_date_post,b_date_event,b_location):
		self.b_title = b_title
   		self.b_content = b_content
   		self.b_sender = b_sender
   		self.b_for_group = b_for_group
   		self.b_date_post = b_date_post
   		self.b_date_event = b_date_event
   		self.b_location = b_location
   		self.b_attendance = 1
   		self.b_active = 1

class Group(db.Model):
	id = db.Column(db.Integer, primary_key = True, autoincrement = True)
	g_name = db.Column(db.String)
	g_owner = db.Column(db.String)
	g_created_date = db.Column(db.String)
	g_active = db.Column(db.Integer)

	def __init__(self, g_name, g_owner, g_created_date):
		self.g_name = g_name
		self.g_owner = g_owner
		self.g_created_date = g_created_date
		self.g_active = 1

@app.route('/register', methods = ['POST','GET'])
def register():
	if request.method == 'POST':
		jsonData = request.json
		for user in User.query.all():
			if user.u_token == jsonData['u_token']:
				return "{'status':'already registered'}"

		user = User(jsonData['u_studentid'], jsonData['u_blue'], jsonData['u_token'],jsonData['u_register_date'], jsonData['u_permission_number'], jsonData['u_designation'])
		db.session.add(user)
		db.session.commit()
		return "{'status':'registered successfully'}"

	if request.method == 'GET':	
		jsonData = []
		users = User.query.all()
		for user in users:
			jsonData.append({'id':user.id,'u_studentid':user.u_studentid, 'u_blue':user.u_blue, 'u_register_date':user.u_register_date, 'u_token':user.u_token, 'u_designation':user.u_designation, 'u_permission_number':user.u_permission_number, 'u_active':user.u_active})
        return jsonify(results = jsonData)
        
@app.route('/broadcasts/all')
def allBroadcasts():
	jsonData = []
	for b in Broadcast.query.all():
		jsonData.append({'id':b.id, 'b_title':b.b_title, 'b_content':b.b_content, 'b_sender':b.b_sender, 'b_location':b.b_location, 'b_attendance':b.b_attendance, 'b_date_post':b.b_date_post, 'b_date_event':b.b_date_event})
	return jsonify(results = jsonData)

@app.route('/broadcasts/create',methods = ['POST'])
def newBroadcast():
	jsonData = request.json
	broadcast = Broadcast(jsonData['b_title'] , jsonData['b_content'], jsonData['b_sender'], jsonData['b_for_group'], jsonData['b_date_post'], jsonData['b_date_event'], jsonData['b_location'])
	db.session.add(broadcast)
	db.session.commit()
	sendBroadcast(broadcast)
	return "{'status':'saved to database successfully'}"

def sendBroadcast(b):
	# taking a 2d array of tokens as max client.send() size for each token is 1000
	tokens = []
	userCount = db.session.query(User).count()
	for i in range(0, 1 + userCount/500):
		tokens.append([])

	for user in User.query.all():
		tokens[user.id/500].append(user.u_token)
	
	print 'size of tokens ', len(tokens), ' ',len(tokens[0])

	for token in tokens:
		msg = {'id':b.id, 'b_title':b.b_title, 'b_content':b.b_content, 'b_sender':b.b_sender, 'b_location':b.b_location, 'b_attendance':b.b_attendance, 'b_date_post':b.b_date_post, 'b_date_event':b.b_date_event}
		res = client.send(token, msg)
		print 'responses => ', res.responses
		print 'regids => ', res.registration_ids
		print 'successes => ',res.successes
		print 'failures => ',res.failures
		print 'errors => ',res.errors

	return "{'status':'broadcasted successfully'}"

@app.route('/groups/adminof/<username>')
def groupadmin(username):
	jsonData = []
	sleep(1)
	user =  User.query.filter_by(u_studentid = username).first()
	if(user != None):
		if user.u_permission_number == 1:
			group = db.session.query(Group).filter_by(g_name = "all").first()
			jsonData.append({'id':group.id,'g_name':group.g_name, 'g_owner':group.g_owner, 'g_created_date':group.g_created_date, 'g_active':group.g_active})
	for group in Group.query.all():
		if group.g_owner == username:
			jsonData.append({'id':group.id,'g_name':group.g_name, 'g_owner':group.g_owner, 'g_created_date':group.g_created_date, 'g_active':group.g_active})

	return jsonify(results = jsonData)

@app.route('/groups/all')
def groups():
	jsonData = []
	groups = Group.query.all()
	for group in groups:
		jsonData.append({'id':group.id,'g_name':group.g_name, 'g_owner':group.g_owner, 'g_created_date':group.g_created_date, 'g_active':group.g_active})
	return jsonify(results = jsonData)

@app.route('/groups/create', methods = ['POST'])
def create_group():
	jsonData = request.json
	for group in Group.query.all():
		if group.g_name == jsonData['g_name']:
			return "{'status':'group already exists'}"
	group = Group(jsonData['g_name'], jsonData['g_owner'], jsonData['g_created_date'])
	db.session.add(group)
	db.session.commit()
	groupsChangedBroadcast()
	return "{'status':'group created successfully'}"

@app.route('/notifygroupschanged')
def groupsChangedBroadcast():
	#(self,b_title,b_content,b_sender,b_for_group,b_date_post,b_date_event,b_location):
	broadcast = Broadcast("BROADCAST_GROUPS_CHANGED", "/groups", "god", "all", str(int(round(time.time() * 1000))),str(int(round(time.time() * 1000))),"NA")
	sendBroadcast(broadcast)
	return ''
	
@app.route('/verify/<username>/<pwd>')
def verify(username, pwd):
	if makerequest(username,pwd): 
		return "{'status':'true'}"
	else:
		return "{'status':'false'}"

if __name__ == '__main__':
	db.create_all()
	if db.session.query(Group).count() == 0:
		allgroup = Group('all', 'god', '0')
		db.session.add(allgroup)
		db.session.commit()
		print 'added default groups'

	app.run(host="192.168.150.1",port=8080, debug=True, threaded = True)